package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.Gatilho;
import com.tragoff.tragoff.model.Meta;
import com.tragoff.tragoff.model.RegistroCigarro;
import com.tragoff.tragoff.model.TipoCigarro;
import com.tragoff.tragoff.repository.GatilhoRepository;
import com.tragoff.tragoff.repository.RegistroCigarroRepository;
import com.tragoff.tragoff.repository.TipoCigarroRepository;
import com.tragoff.tragoff.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private static final long MILIS_POR_DIA = 24L * 60 * 60 * 1000;

    @Autowired
    private RegistroCigarroRepository registroRepository;

    @Autowired
    private TipoCigarroRepository tipoRepository;

    @Autowired
    private GatilhoRepository gatilhoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MetaService metaService;

    public Map<String, Object> montar(Long usuarioId) throws Exception {
        if (usuarioRepository.findById(usuarioId).orElse(null) == null) {
            throw new Exception("Usuário não encontrado");
        }

        Date hoje = new Date();
        Date inicioHoje = DataUtil.inicioDoDia(hoje);
        Date fimHoje = DataUtil.fimDoDia(hoje);

        // Do mais recente para o mais antigo
        List<RegistroCigarro> todos = registroRepository.buscarPorUsuario(usuarioId);
        List<RegistroCigarro> doDia = registroRepository.findByUsuarioIdAndDataHoraBetween(
                usuarioId, inicioHoje, fimHoje);
        List<RegistroCigarro> daSemana = registroRepository.findByUsuarioIdAndDataHoraBetween(
                usuarioId, DataUtil.somarDias(inicioHoje, -6), fimHoje);   // últimos 7 dias
        List<RegistroCigarro> doMes = registroRepository.findByUsuarioIdAndDataHoraBetween(
                usuarioId, DataUtil.somarDias(inicioHoje, -29), fimHoje);  // últimos 30 dias

        Map<String, Object> dash = new HashMap<>();

        // Gastos (em reais, então podem ser somados)
        dash.put("gastoHoje", arredondar(somarGasto(doDia)));
        dash.put("gastoSemana", arredondar(somarGasto(daSemana)));
        dash.put("gastoMes", arredondar(somarGasto(doMes)));
        dash.put("gastoTotal", arredondar(somarGasto(todos)));

        // Dias desde o primeiro registro (para a média diária)
        long dias = 1;
        if (!todos.isEmpty()) {
            Date primeiro = todos.get(todos.size() - 1).getDataHora();
            dias = (inicioHoje.getTime() - DataUtil.inicioDoDia(primeiro).getTime()) / MILIS_POR_DIA + 1;
            if (dias < 1) {
                dias = 1;
            }
        }

        // Consumo de cada produto, sempre na unidade dele (cigarros, tragadas ou unidades)
        List<Map<String, Object>> porTipo = new ArrayList<>();
        String tipoMaisUsado = null;
        int maiorVezes = 0;
        for (TipoCigarro t : tipoRepository.findAll()) {
            int total = somarTipo(todos, t);
            int vezes = contarTipo(todos, t);
            Map<String, Object> item = new HashMap<>();
            item.put("nome", t.getNome());
            item.put("unidade", t.getUnidade());
            item.put("hoje", somarTipo(doDia, t));
            item.put("total", total);
            item.put("media", Math.round((float) total / dias * 10) / 10f);
            porTipo.add(item);
            if (vezes > maiorVezes) {
                maiorVezes = vezes;
                tipoMaisUsado = t.getNome();
            }
        }
        dash.put("porTipo", porTipo);
        dash.put("tipoMaisUsado", tipoMaisUsado);

        // Horário com mais registros (posição no vetor = hora do dia, de 0 a 23)
        int[] porHora = new int[24];
        for (RegistroCigarro r : todos) {
            Calendar c = Calendar.getInstance();
            c.setTime(r.getDataHora());
            porHora[c.get(Calendar.HOUR_OF_DAY)]++;
        }
        int pico = -1;
        int maior = 0;
        for (int h = 0; h < 24; h++) {
            if (porHora[h] > maior) {
                maior = porHora[h];
                pico = h;
            }
        }
        dash.put("horarioPico", pico);

        // Principais gatilhos (do maior para o menor). Registro sem gatilho é ignorado.
        List<Map<String, Object>> gatilhos = new ArrayList<>();
        for (Gatilho g : gatilhoRepository.findAll()) {
            int total = 0;
            for (RegistroCigarro r : todos) {
                if (r.getGatilho() != null && r.getGatilho().getId().equals(g.getId())) {
                    total = total + r.getQuantidade();
                }
            }
            if (total > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("nome", g.getNome());
                item.put("total", total);
                gatilhos.add(item);
            }
        }
        ordenarPorTotal(gatilhos);
        dash.put("gatilhos", gatilhos);

        // Últimos 7 dias (do mais antigo para hoje), cada unidade separada
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> ultimosDias = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Date dia = DataUtil.somarDias(inicioHoje, -i);
            List<RegistroCigarro> doDiaI = registroRepository.findByUsuarioIdAndDataHoraBetween(
                    usuarioId, dia, DataUtil.fimDoDia(dia));
            Map<String, Object> item = new HashMap<>();
            item.put("data", formato.format(dia));
            item.put("cigarros", somarUnidade(doDiaI, "cigarros"));
            item.put("tragadas", somarUnidade(doDiaI, "tragadas"));
            item.put("unidades", somarUnidade(doDiaI, "unidades"));
            ultimosDias.add(item);
        }
        dash.put("ultimosDias", ultimosDias);

        // Metas ativas: uma em cigarros e outra em tragadas (se existirem)
        List<Map<String, Object>> metas = new ArrayList<>();
        String[] unidades = {"cigarros", "tragadas"};
        for (String unidade : unidades) {
            Meta ativa = metaService.buscarAtiva(usuarioId, unidade);
            if (ativa != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("unidade", unidade);
                item.put("meta", ativa.getCigarrosPorDiaMeta());
                item.put("hoje", somarUnidade(doDia, unidade));
                item.put("dataFim", ativa.getDataFim());
                item.put("progresso", Math.round(metaService.calcularProgresso(ativa) * 10) / 10f);
                metas.add(item);
            }
        }
        dash.put("metas", metas);

        return dash;
    }

    // Soma a quantidade dos registros de um tipo
    private int somarTipo(List<RegistroCigarro> lista, TipoCigarro tipo) {
        int total = 0;
        for (RegistroCigarro r : lista) {
            if (r.getTipoCigarro().getId().equals(tipo.getId())) {
                total = total + r.getQuantidade();
            }
        }
        return total;
    }

    // Conta quantos registros existem de um tipo
    private int contarTipo(List<RegistroCigarro> lista, TipoCigarro tipo) {
        int vezes = 0;
        for (RegistroCigarro r : lista) {
            if (r.getTipoCigarro().getId().equals(tipo.getId())) {
                vezes++;
            }
        }
        return vezes;
    }

    // Soma a quantidade dos registros de uma unidade ("cigarros", "tragadas" ou "unidades")
    private int somarUnidade(List<RegistroCigarro> lista, String unidade) {
        int total = 0;
        for (RegistroCigarro r : lista) {
            if (r.ehDaUnidade(unidade)) {
                total = total + r.getQuantidade();
            }
        }
        return total;
    }

    private float somarGasto(List<RegistroCigarro> lista) {
        float total = 0;
        for (RegistroCigarro r : lista) {
            total = total + r.calcularGasto();
        }
        return total;
    }

    // Duas casas decimais (evita 2.1000001)
    private float arredondar(float valor) {
        return Math.round(valor * 100) / 100f;
    }

    // Coloca o item de maior "total" primeiro (ordenação simples com dois laços)
    private void ordenarPorTotal(List<Map<String, Object>> lista) {
        for (int i = 0; i < lista.size(); i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                Integer a = (Integer) lista.get(i).get("total");
                Integer b = (Integer) lista.get(j).get("total");
                if (b > a) {
                    Map<String, Object> troca = lista.get(i);
                    lista.set(i, lista.get(j));
                    lista.set(j, troca);
                }
            }
        }
    }
}

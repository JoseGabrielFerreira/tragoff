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
        Meta ativa = metaService.buscarAtiva(usuarioId);

        Map<String, Object> dash = new HashMap<>();

        // Consumo de hoje e meta diária
        dash.put("consumoHoje", somarQuantidade(doDia));
        dash.put("metaDiaria", ativa != null ? ativa.getCigarrosPorDiaMeta() : 0);

        // Gastos
        dash.put("gastoHoje", arredondar(somarGasto(doDia)));
        dash.put("gastoSemana", arredondar(somarGasto(daSemana)));
        dash.put("gastoMes", arredondar(somarGasto(doMes)));

        // Média diária: total de cigarros dividido pelos dias desde o primeiro registro
        float media = 0;
        if (!todos.isEmpty()) {
            Date primeiro = todos.get(todos.size() - 1).getDataHora();
            long dias = (inicioHoje.getTime() - DataUtil.inicioDoDia(primeiro).getTime()) / MILIS_POR_DIA + 1;
            if (dias < 1) {
                dias = 1;
            }
            media = (float) somarQuantidade(todos) / dias;
        }
        dash.put("mediaDiaria", Math.round(media * 10) / 10f);

        // Horário de maior consumo (posição no vetor = hora do dia, de 0 a 23)
        int[] porHora = new int[24];
        for (RegistroCigarro r : todos) {
            Calendar c = Calendar.getInstance();
            c.setTime(r.getDataHora());
            porHora[c.get(Calendar.HOUR_OF_DAY)] += r.getQuantidade();
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

        // Tipo de cigarro mais consumido
        String tipoMais = null;
        int maiorTipo = 0;
        for (TipoCigarro t : tipoRepository.findAll()) {
            int total = 0;
            for (RegistroCigarro r : todos) {
                if (r.getTipoCigarro().getId().equals(t.getId())) {
                    total = total + r.getQuantidade();
                }
            }
            if (total > maiorTipo) {
                maiorTipo = total;
                tipoMais = t.getNome();
            }
        }
        dash.put("tipoMaisConsumido", tipoMais);

        // Principais gatilhos (do maior para o menor)
        List<Map<String, Object>> gatilhos = new ArrayList<>();
        for (Gatilho g : gatilhoRepository.findAll()) {
            int total = 0;
            for (RegistroCigarro r : todos) {
                if (r.getGatilho().getId().equals(g.getId())) {
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

        // Consumo dos últimos 7 dias (do mais antigo para hoje)
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> ultimosDias = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Date dia = DataUtil.somarDias(inicioHoje, -i);
            List<RegistroCigarro> doDiaI = registroRepository.findByUsuarioIdAndDataHoraBetween(
                    usuarioId, dia, DataUtil.fimDoDia(dia));
            Map<String, Object> item = new HashMap<>();
            item.put("data", formato.format(dia));
            item.put("total", somarQuantidade(doDiaI));
            ultimosDias.add(item);
        }
        dash.put("ultimosDias", ultimosDias);

        // Meta ativa e progresso
        if (ativa != null) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("id", ativa.getId());
            meta.put("cigarrosPorDiaInicial", ativa.getCigarrosPorDiaInicial());
            meta.put("cigarrosPorDiaMeta", ativa.getCigarrosPorDiaMeta());
            meta.put("dataFim", ativa.getDataFim());
            meta.put("progresso", Math.round(metaService.calcularProgresso(ativa) * 10) / 10f);
            dash.put("metaAtiva", meta);
        } else {
            dash.put("metaAtiva", null);
        }

        return dash;
    }

    private int somarQuantidade(List<RegistroCigarro> lista) {
        int total = 0;
        for (RegistroCigarro r : lista) {
            total = total + r.getQuantidade();
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
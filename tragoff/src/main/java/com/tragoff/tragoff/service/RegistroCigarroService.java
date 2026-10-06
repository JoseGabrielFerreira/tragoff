package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.*;
import com.tragoff.tragoff.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RegistroCigarroService {

    @Autowired
    private RegistroCigarroRepository registroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoCigarroRepository tipoRepository;

    @Autowired
    private GatilhoRepository gatilhoRepository;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private ConsumoDiarioRepository consumoRepository;

    @Autowired
    private GastoRepository gastoRepository;

    public RegistroCigarro registrar(RegistroCigarro r) throws Exception {
        if (r.getQuantidade() <= 0) {
            throw new Exception("A quantidade deve ser maior que zero");
        }
        if (r.getUsuario() == null || r.getUsuario().getId() == null) {
            throw new Exception("Usuário não informado");
        }
        if (r.getTipoCigarro() == null || r.getTipoCigarro().getId() == null) {
            throw new Exception("Escolha o tipo de cigarro");
        }
        if (r.getGatilho() == null || r.getGatilho().getId() == null) {
            throw new Exception("Escolha o gatilho");
        }

        // Busca os objetos completos no banco (o front só manda o id de cada um)
        Usuario usuario = usuarioRepository.findById(r.getUsuario().getId()).orElse(null);
        TipoCigarro tipo = tipoRepository.findById(r.getTipoCigarro().getId()).orElse(null);
        Gatilho gatilho = gatilhoRepository.findById(r.getGatilho().getId()).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }
        if (tipo == null) {
            throw new Exception("Tipo de cigarro não encontrado");
        }
        if (gatilho == null) {
            throw new Exception("Gatilho não encontrado");
        }

        if (r.getDataHora() == null) {
            r.setDataHora(new Date());
        }
        r.setId(null);
        r.setUsuario(usuario);
        r.setTipoCigarro(tipo);
        r.setGatilho(gatilho);

        RegistroCigarro salvo = registroRepository.save(r);
        atualizarConsumoDiario(usuario, salvo.getDataHora());
        return salvo;
    }

    public List<RegistroCigarro> listarPorUsuario(Long usuarioId) {
        return registroRepository.buscarPorUsuario(usuarioId);
    }

    public void excluir(Long id) throws Exception {
        RegistroCigarro r = registroRepository.findById(id).orElse(null);
        if (r == null) {
            throw new Exception("Registro não encontrado");
        }
        Usuario usuario = r.getUsuario();
        Date data = r.getDataHora();
        registroRepository.delete(r);
        atualizarConsumoDiario(usuario, data);
    }

    // Recalcula o total e o gasto do dia e guarda em ConsumoDiario e Gasto
    private void atualizarConsumoDiario(Usuario usuario, Date data) {
        Date inicio = DataUtil.inicioDoDia(data);
        Date fim = DataUtil.fimDoDia(data);
        List<RegistroCigarro> doDia =
                registroRepository.findByUsuarioIdAndDataHoraBetween(usuario.getId(), inicio, fim);

        int total = 0;
        float valor = 0;
        for (RegistroCigarro r : doDia) {
            total = total + r.getQuantidade();
            valor = valor + r.calcularGasto();
        }

        int metaDoDia = 0;
        List<Meta> ativas = metaRepository.findByUsuarioIdAndStatus(usuario.getId(), "ATIVA");
        if (!ativas.isEmpty()) {
            metaDoDia = ativas.get(0).getCigarrosPorDiaMeta();
        }

        ConsumoDiario consumo = consumoRepository.findByUsuarioIdAndData(usuario.getId(), inicio);
        if (consumo == null) {
            consumo = new ConsumoDiario();
            consumo.setUsuario(usuario);
            consumo.setData(inicio);
        }
        consumo.setTotalCigarros(total);
        consumo.setMetaDoDia(metaDoDia);
        consumo.setAtingiuMeta(metaDoDia > 0 && total <= metaDoDia);
        consumo = consumoRepository.save(consumo);

        Gasto gasto = gastoRepository.findByConsumoDiarioId(consumo.getId());
        if (gasto == null) {
            gasto = new Gasto();
            gasto.setConsumoDiario(consumo);
        }
        gasto.setValor(valor);
        gastoRepository.save(gasto);
    }
}
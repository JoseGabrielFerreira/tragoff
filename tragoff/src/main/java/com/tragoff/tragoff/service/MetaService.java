package com.tragoff.tragoff.service;

import com.tragoff.tragoff.model.Meta;
import com.tragoff.tragoff.model.PlanoReducao;
import com.tragoff.tragoff.model.RegistroCigarro;
import com.tragoff.tragoff.model.Usuario;
import com.tragoff.tragoff.repository.MetaRepository;
import com.tragoff.tragoff.repository.PlanoReducaoRepository;
import com.tragoff.tragoff.repository.RegistroCigarroRepository;
import com.tragoff.tragoff.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MetaService {

    private static final long MILIS_POR_DIA = 24L * 60 * 60 * 1000;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanoReducaoRepository planoRepository;

    @Autowired
    private RegistroCigarroRepository registroRepository;

    public Meta criar(Meta m) throws Exception {
        if (m.getUsuario() == null || m.getUsuario().getId() == null) {
            throw new Exception("Usuário não informado");
        }
        Usuario usuario = usuarioRepository.findById(m.getUsuario().getId()).orElse(null);
        if (usuario == null) {
            throw new Exception("Usuário não encontrado");
        }
        if (m.getCigarrosPorDiaInicial() <= 0) {
            throw new Exception("Informe quantos cigarros por dia você fuma hoje");
        }
        if (m.getCigarrosPorDiaMeta() < 0) {
            throw new Exception("A meta não pode ser negativa");
        }
        if (m.getCigarrosPorDiaMeta() >= m.getCigarrosPorDiaInicial()) {
            throw new Exception("A meta deve ser menor que o consumo atual");
        }
        if (m.getDataInicio() == null) {
            m.setDataInicio(new Date());
        }
        if (m.getDataFim() == null) {
            throw new Exception("Informe a data final da meta");
        }
        if (m.getDataFim().before(DataUtil.inicioDoDia(m.getDataInicio()))) {
            throw new Exception("A data final deve ser depois da data de início");
        }
        if (m.getDataFim().before(DataUtil.inicioDoDia(new Date()))) {
            throw new Exception("A data final já passou");
        }

        // Só pode existir uma meta ativa: cancela a anterior
        List<Meta> ativas = metaRepository.findByUsuarioIdAndStatus(usuario.getId(), "ATIVA");
        for (Meta antiga : ativas) {
            antiga.setStatus("CANCELADA");
            metaRepository.save(antiga);
        }

        m.setId(null);
        m.setUsuario(usuario);
        m.setStatus("ATIVA");
        Meta salva = metaRepository.save(m);

        criarPlano(salva);
        return salva;
    }

    // Plano de redução: quantos cigarros reduzir por semana para chegar na meta
    private void criarPlano(Meta meta) {
        long dias = (DataUtil.inicioDoDia(meta.getDataFim()).getTime()
                - DataUtil.inicioDoDia(meta.getDataInicio()).getTime()) / MILIS_POR_DIA + 1;
        int semanas = (int) Math.ceil(dias / 7.0);
        if (semanas < 1) {
            semanas = 1;
        }
        int totalReduzir = meta.getCigarrosPorDiaInicial() - meta.getCigarrosPorDiaMeta();
        int porSemana = (int) Math.ceil((double) totalReduzir / semanas);
        if (porSemana < 1) {
            porSemana = 1;
        }

        PlanoReducao plano = new PlanoReducao();
        plano.setMeta(meta);
        plano.setReducaoPorSemana(porSemana);
        plano.setSemanasPrevistas(semanas);
        planoRepository.save(plano);
    }

    public List<Meta> listarPorUsuario(Long usuarioId) {
        buscarAtiva(usuarioId); // aproveita para encerrar a meta ativa vencida
        return metaRepository.findByUsuarioIdOrderByDataInicioDesc(usuarioId);
    }

    // Devolve a meta ativa ou null. Se a data final já passou, marca como CONCLUIDA.
    public Meta buscarAtiva(Long usuarioId) {
        List<Meta> ativas = metaRepository.findByUsuarioIdAndStatus(usuarioId, "ATIVA");
        if (ativas.isEmpty()) {
            return null;
        }
        Meta meta = ativas.get(0);
        if (meta.getDataFim() != null && meta.getDataFim().before(DataUtil.inicioDoDia(new Date()))) {
            meta.setStatus("CONCLUIDA");
            metaRepository.save(meta);
            return null;
        }
        return meta;
    }

    public Meta mudarStatus(Long id, String novoStatus) throws Exception {
        Meta meta = metaRepository.findById(id).orElse(null);
        if (meta == null) {
            throw new Exception("Meta não encontrada");
        }
        if (!"ATIVA".equals(meta.getStatus())) {
            throw new Exception("Só é possível alterar uma meta ativa");
        }
        if (!"CANCELADA".equals(novoStatus) && !"CONCLUIDA".equals(novoStatus)) {
            throw new Exception("Status inválido");
        }
        meta.setStatus(novoStatus);
        return metaRepository.save(meta);
    }

    // Progresso (0 a 100) com base na média diária desde o início da meta
    public float calcularProgresso(Meta meta) {
        Date hoje = new Date();
        Date inicio = DataUtil.inicioDoDia(meta.getDataInicio());
        List<RegistroCigarro> registros = registroRepository.findByUsuarioIdAndDataHoraBetween(
                meta.getUsuario().getId(), inicio, DataUtil.fimDoDia(hoje));

        int total = 0;
        for (RegistroCigarro r : registros) {
            total = total + r.getQuantidade();
        }

        long dias = (DataUtil.inicioDoDia(hoje).getTime() - inicio.getTime()) / MILIS_POR_DIA + 1;
        if (dias < 1) {
            dias = 1;
        }
        float media = (float) total / dias;
        return meta.calcularProgresso(media);
    }
}
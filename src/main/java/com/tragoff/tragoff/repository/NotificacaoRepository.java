package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioIdOrderByDataEnvioDesc(Long usuarioId);
}
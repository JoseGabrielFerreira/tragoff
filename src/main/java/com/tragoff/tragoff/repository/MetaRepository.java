package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Meta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByUsuarioIdOrderByDataInicioDesc(Long usuarioId);
    List<Meta> findByUsuarioIdAndStatus(Long usuarioId, String status);
    List<Meta> findByUsuarioIdAndStatusAndUnidade(Long usuarioId, String status, String unidade);
}
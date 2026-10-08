package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.UsuarioConquista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioConquistaRepository extends JpaRepository<UsuarioConquista, Long> {
    List<UsuarioConquista> findByUsuarioId(Long usuarioId);
}
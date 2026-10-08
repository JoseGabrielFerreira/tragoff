package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.PerfilFumante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilFumanteRepository extends JpaRepository<PerfilFumante, Long> {
    PerfilFumante findByUsuarioId(Long usuarioId);
}
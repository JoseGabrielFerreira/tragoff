package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Administrador findByEmailAndSenha(String email, String senha);
}
package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmailAndSenha(String email, String senha);
}
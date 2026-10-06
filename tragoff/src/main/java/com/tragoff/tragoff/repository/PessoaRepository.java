package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Pessoa findByEmail(String email);
}
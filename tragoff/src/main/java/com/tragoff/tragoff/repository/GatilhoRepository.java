package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Gatilho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatilhoRepository extends JpaRepository<Gatilho, Long> {
    Gatilho findByNome(String nome);
}
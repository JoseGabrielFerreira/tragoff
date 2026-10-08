package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.TipoCigarro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoCigarroRepository extends JpaRepository<TipoCigarro, Long> {
    TipoCigarro findByNome(String nome);
}
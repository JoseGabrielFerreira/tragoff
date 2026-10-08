package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    Gasto findByConsumoDiarioId(Long consumoDiarioId);
}
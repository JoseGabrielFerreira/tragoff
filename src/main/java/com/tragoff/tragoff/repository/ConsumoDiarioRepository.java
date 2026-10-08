package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.ConsumoDiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ConsumoDiarioRepository extends JpaRepository<ConsumoDiario, Long> {
    ConsumoDiario findByUsuarioIdAndData(Long usuarioId, Date data);
    List<ConsumoDiario> findByUsuarioIdOrderByDataDesc(Long usuarioId);
}
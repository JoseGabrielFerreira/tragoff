package com.tragoff.tragoff.repository;

import com.tragoff.tragoff.model.PlanoReducao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoReducaoRepository extends JpaRepository<PlanoReducao, Long> {
    PlanoReducao findByMetaId(Long metaId);
}
package com.tragoff.tragoff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "plano_reducao")
public class PlanoReducao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "reducao_por_semana", nullable = false)
    private int reducaoPorSemana;
    @Column(name = "semanas_previstas")
    private int semanasPrevistas;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @JoinColumn(name = "meta_id", referencedColumnName = "id", unique = true)
    private Meta meta;
}
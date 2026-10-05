package com.tragoff.tragoff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "consumo_diario")
public class ConsumoDiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "data", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date data;
    @Column(name = "total_cigarros", nullable = false)
    private int totalCigarros;
    @Column(name = "meta_do_dia")
    private int metaDoDia;
    @Column(name = "atingiu_meta")
    private boolean atingiuMeta;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}
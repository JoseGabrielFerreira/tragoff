package com.tragoff.tragoff.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "gasto")
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "valor", nullable = false)
    private float valor;
    @OneToOne
    @JoinColumn(name = "consumo_diario_id", referencedColumnName = "id", unique = true)
    private ConsumoDiario consumoDiario;
}
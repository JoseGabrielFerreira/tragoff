package com.tragoff.tragoff.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_cigarro")
public class TipoCigarro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome", nullable = false, length = 50, unique = true)
    private String nome;
    @Column(name = "preco_unitario", nullable = false)
    private float precoUnitario;
}
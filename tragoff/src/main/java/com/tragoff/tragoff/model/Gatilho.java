package com.tragoff.tragoff.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "gatilho")
public class Gatilho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome", nullable = false, length = 50, unique = true)
    private String nome;
}
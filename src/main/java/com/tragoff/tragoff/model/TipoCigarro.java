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
    // Em que o consumo é contado: "cigarros", "tragadas" ou "unidades"
    @Column(name = "unidade", length = 20)
    private String unidade;
    // Preço padrão por unidade. Só é usado se o usuário ainda não registrou nenhuma compra desse tipo.
    @Column(name = "preco_unitario", nullable = false)
    private float precoUnitario;
}

package com.tragoff.tragoff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "perfil_fumante")
public class PerfilFumante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "anos_fumando")
    private int anosFumando;
    @Column(name = "cigarros_por_dia_inicial")
    private int cigarrosPorDiaInicial;
    @Column(name = "motivo_para_parar", length = 200)
    private String motivoParaParar;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private Usuario usuario;
}

package com.tragoff.tragoff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "meta")
public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cigarros_por_dia_inicial", nullable = false)
    private int cigarrosPorDiaInicial;
    @Column(name = "cigarros_por_dia_meta", nullable = false)
    private int cigarrosPorDiaMeta;
    @Column(name = "data_inicio", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date dataInicio;
    @Column(name = "data_fim")
    @Temporal(value = TemporalType.DATE)
    private Date dataFim;
    @Column(name = "status", length = 20)
    private String status; // ATIVA, CONCLUIDA ou CANCELADA
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;


    // mediaAtual = média de cigarros por dia desde o início da meta
    public float calcularProgresso(float mediaAtual) {
        float necessaria = cigarrosPorDiaInicial - cigarrosPorDiaMeta;
        if (necessaria <= 0) {
            return 0;
        }
        float atingida = cigarrosPorDiaInicial - mediaAtual;
        float progresso = atingida / necessaria * 100;
        if (progresso < 0) {
            progresso = 0;
        }
        if (progresso > 100) {
            progresso = 100;
        }
        return progresso;
    }

    public boolean estaConcluida() {
        return "CONCLUIDA".equals(status);
    }
}
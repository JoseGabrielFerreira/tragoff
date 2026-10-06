package com.tragoff.tragoff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "registro_cigarro")
public class RegistroCigarro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "data_hora", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dataHora;
    @Column(name = "quantidade", nullable = false)
    private int quantidade;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "tipo_cigarro_id", referencedColumnName = "id")
    private TipoCigarro tipoCigarro;
    @ManyToOne
    @JoinColumn(name = "gatilho_id", referencedColumnName = "id")
    private Gatilho gatilho;

    public float calcularGasto() {
        return quantidade * tipoCigarro.getPrecoUnitario();
    }
}
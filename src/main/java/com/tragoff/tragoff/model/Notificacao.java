package com.tragoff.tragoff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "notificacao")
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "mensagem", nullable = false, length = 200)
    private String mensagem;
    @Column(name = "data_envio")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dataEnvio;
    @Column(name = "lida")
    private boolean lida;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}
package com.tragoff.tragoff.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "usuario")
public class Usuario extends Pessoa {
    @Column(name = "data_cadastro")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dataCadastro;
}
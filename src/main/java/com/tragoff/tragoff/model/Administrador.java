package com.tragoff.tragoff.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "administrador")
public class Administrador extends Pessoa {
    @Column(name = "nivel_acesso", length = 30)
    private String nivelAcesso;
}
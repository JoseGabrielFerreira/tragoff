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
    // Gasto calculado na hora do registro. Fica salvo, então mudar preços depois não altera registros antigos.
    @Column(name = "valor_gasto")
    private float valorGasto;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "tipo_cigarro_id", referencedColumnName = "id")
    private TipoCigarro tipoCigarro;
    // Gatilho é opcional: pode ser null
    @ManyToOne
    @JoinColumn(name = "gatilho_id", referencedColumnName = "id")
    private Gatilho gatilho;
    // Compra usada para calcular o gasto (null nos registros antigos ou quando usou o preço padrão)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "compra_id", referencedColumnName = "id")
    private Compra compra;

    public float calcularGasto() {
        return valorGasto;
    }

    // Verdadeiro se o registro é de um tipo que usa essa unidade ("cigarros", "tragadas" ou "unidades")
    public boolean ehDaUnidade(String unidade) {
        return tipoCigarro != null && unidade != null && unidade.equals(tipoCigarro.getUnidade());
    }
}

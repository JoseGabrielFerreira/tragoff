package com.tragoff.tragoff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

// Uma compra: um maço de cigarro, um vape novo, um pacote de tabaco...
// O preço e a quantidade informados aqui valem para todos os consumos feitos depois dela.
@Data
@Entity
@Table(name = "compra")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "preco_pago", nullable = false)
    private float precoPago;
    // Quantos cigarros vêm no maço, quantas tragadas o vape tem, quantas unidades vêm no pacote
    @Column(name = "quantidade_total", nullable = false)
    private int quantidadeTotal;
    @Column(name = "data_compra", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dataCompra;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "tipo_cigarro_id", referencedColumnName = "id")
    private TipoCigarro tipoCigarro;

    // Quantas unidades da compra ainda não foram consumidas.
    // @Transient = não vira coluna no banco; o CompraService calcula na hora.
    @Transient
    private int restante;

    // Exemplo: R$ 100 / 5000 tragadas = R$ 0,02 por tragada
    public float calcularPrecoPorUnidade() {
        return precoPago / quantidadeTotal;
    }
}

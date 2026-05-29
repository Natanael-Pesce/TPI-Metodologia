package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import Tpi_Metodologia.API.utility.EstadoPago;
import Tpi_Metodologia.API.utility.TipoPago;

@Entity
@Table(name = "Pagos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pagoID;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedidoID")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    private LocalDate fechaPago;

    @Column(nullable = false)
    private double monto;
}
package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import Tpi_Metodologia.API.utility.EstadoReclamo;

@Entity
@Table(name = "Reclamo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reclamoID;

    @Column(nullable = false)
    private String motivo;

    private String tipo; // PRODUCTO_DEFECTUOSO, ENTREGA_TARDÍA, ERROR_COBRO, etc.

    @Column(nullable = false)
    private EstadoReclamo estado; // ABIERTO, EN_PROCESO, RESUELTO, CERRADO

    @ManyToOne
    @JoinColumn(name = "pedidoID", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "usuarioID", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fechaReclamo;
}

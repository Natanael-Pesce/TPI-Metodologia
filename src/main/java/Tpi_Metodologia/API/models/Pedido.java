package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import Tpi_Metodologia.API.utility.EstadoPedido;

@Entity
@Table(name = "Pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pedidoID;

    @ManyToOne
    @JoinColumn(name = "usuarioID", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Detalle_Pedido> detalles;

    @Column(nullable = false)
    private double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false)
    private LocalDate fechaPedido;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagoID")
    private Pago pago;

    @OneToMany(mappedBy = "pedido")
    private List<Reclamo> reclamo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "envioID")
    private Envio envio;
    
}
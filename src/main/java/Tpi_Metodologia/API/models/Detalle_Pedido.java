package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Detalle_Pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Detalle_Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int detallePedidoID;

    @ManyToOne
    @JoinColumn(name = "productoID", nullable = false)
    private Producto producto; // CORREGIDO: era @OneToMany, debe ser @ManyToOne

    @ManyToOne
    @JoinColumn(name = "pedidoID")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Pedido pedido;

    @Column(nullable = false)
    private int cantidad;

    private double subTotal; // calculado en el Service: producto.precio * cantidad
}
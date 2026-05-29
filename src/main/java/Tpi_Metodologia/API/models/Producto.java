package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Producto")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productoID;

    @Column(nullable = false)
    private String nombreProducto;

    @Column(nullable = false)
    private double precioProducto;

    private String imagen;

    @Column(nullable = false)
    private int stock;

    private int stockMin;

    private boolean productoActivo;

    @ManyToOne
    @JoinColumn(name = "cuponID")
    private Cupon cupon;
}
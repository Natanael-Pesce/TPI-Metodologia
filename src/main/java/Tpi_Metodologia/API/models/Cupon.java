package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Cupon")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cuponID;

    @OneToMany(mappedBy = "cupon")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Usuario> clientes;

    @Column(unique = true, nullable = false)
    private String codigo; // Cambiado de int a String ("VERANO20" > 1234)

    @Column(nullable = false)
    private int descuento; // porcentaje 0-100

    private boolean estado;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;
}
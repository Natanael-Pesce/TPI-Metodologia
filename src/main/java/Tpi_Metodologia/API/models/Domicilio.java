package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Domicilio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Domicilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int domicilioID;

    @ManyToMany(mappedBy = "domicilios")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Usuario> usuarios;

    @Column(nullable = false)
    private String pais;

    private String provincia;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String calle;

    private String nro;
    private String departamento;
    private String nroDepartamento;
    private String piso;
}
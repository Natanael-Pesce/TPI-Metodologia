package Tpi_Metodologia.API.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import Tpi_Metodologia.API.utility.Rol;


@Entity
@Table(name = "Usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int usuarioID;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.Cliente;

    @ManyToMany
    @JoinTable(
        name = "usuario_domicilio",
        joinColumns = @JoinColumn(name = "usuarioID"),
        inverseJoinColumns = @JoinColumn(name = "domicilioID")
    )
    private List<Domicilio> domicilios;

    @ManyToOne
    @JoinColumn(name = "cuponID")
    private Cupon cupon;

    private String cuit;
}

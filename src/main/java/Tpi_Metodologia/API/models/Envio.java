package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

import Tpi_Metodologia.API.utility.EstadoEnvio;

@Entity
@Table(name = "Envio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int envioID;

    private String tracking;

    @OneToOne
    @JoinColumn(name = "domicilioID", nullable = false)
    private Domicilio domicilio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnvio estadoEnvio;

    private LocalDate fechaEntrega;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "envio")
    private Pedido pedido;
}
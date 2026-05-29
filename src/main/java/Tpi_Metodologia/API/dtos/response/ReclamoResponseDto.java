package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

import java.time.LocalDate;

import Tpi_Metodologia.API.utility.EstadoReclamo;

@Data
public class ReclamoResponseDto {
    private int reclamoID;
    private String motivo;
    private String tipo;
    private EstadoReclamo estado;
    private int pedidoID;
    private int usuarioID;
    private String usuarioNombre;
    private LocalDate fechaReclamo;
}
package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

import java.time.LocalDate;

/**
 * NUEVO: Response DTO para Cupon.
 */
@Data
public class CuponResponseDto {

    private int cuponID;
    private String codigo;
    private int descuento;
    private boolean estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
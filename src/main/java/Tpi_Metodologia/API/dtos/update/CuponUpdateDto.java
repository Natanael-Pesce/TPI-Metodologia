package Tpi_Metodologia.API.dtos.update;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CuponUpdateDto {

    private String codigo;

    @Min(value = 1, message = "El descuento mínimo es 1%")
    @Max(value = 100, message = "El descuento máximo es 100%")
    private Integer descuento;

    private Boolean estado;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;
}
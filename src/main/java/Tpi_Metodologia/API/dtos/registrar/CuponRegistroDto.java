package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CuponRegistroDto {

    @NotBlank(message = "El código del cupón es obligatorio")
    private String codigo;

    @Min(value = 1, message = "El descuento mínimo es 1%")
    @Max(value = 100, message = "El descuento máximo es 100%")
    private int descuento;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
}
package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * NUEVO: DTO para registrar un pago.
 * No existía en el proyecto original.
 */
@Data
public class PagoRegistroDto {

    @NotBlank(message = "El tipo de pago es obligatorio")
    private String tipoPago; // EFECTIVO, TARJETA, TRANSFERENCIA

    @Positive(message = "El monto debe ser mayor a 0")
    private double monto;
}
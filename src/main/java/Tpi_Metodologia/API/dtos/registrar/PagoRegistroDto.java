package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PagoRegistroDto {

    @NotBlank(message = "El tipo de pago es obligatorio")
    private String tipoPago; // EFECTIVO, TARJETA, TRANSFERENCIA

    @Positive(message = "El monto debe ser mayor a 0")
    private double monto;
}
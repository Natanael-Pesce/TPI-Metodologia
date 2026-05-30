package Tpi_Metodologia.API.dtos.update;

import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnvioUpdateDto {

    private String estadoEnvio;

    private String tracking;

    @Future(message = "La fecha de entrega debe ser una fecha futura")
    private LocalDate fechaEntrega;

    private Integer domicilioID;
}
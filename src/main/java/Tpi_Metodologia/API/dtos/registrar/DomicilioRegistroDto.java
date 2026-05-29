package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * NUEVO: DTO para crear un domicilio.
 * Separado para poder reutilizarse en ClienteRegistroDto y en el endpoint de domicilios.
 */
@Data
public class DomicilioRegistroDto {

    @NotBlank(message = "El país es obligatorio")
    private String pais;

    private String provincia;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    private String nro;
    private String departamento;
    private String nroDepartamento;
    private String piso;
}
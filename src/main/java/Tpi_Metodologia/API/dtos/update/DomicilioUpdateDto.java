package Tpi_Metodologia.API.dtos.update;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DomicilioUpdateDto {

    private String pais;

    private String provincia;

    private String ciudad;

    private String calle;

    private String nro;

    private String departamento;

    @Size(max = 10, message = "El número de departamento no puede superar 10 caracteres")
    private String nroDepartamento;

    @Size(max = 5, message = "El piso no puede superar 5 caracteres")
    private String piso;
}
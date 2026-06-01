package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

@Data
public class DomicilioResponseDto {

    private int domicilioID;
    private String pais;
    private String provincia;
    private String ciudad;
    private String calle;
    private String nro;
    private String departamento;
    private String nroDepartamento;
    private String piso;
}
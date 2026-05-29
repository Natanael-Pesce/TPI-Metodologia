package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

import java.time.LocalDate;

import Tpi_Metodologia.API.utility.EstadoEnvio;

@Data
public class EnvioResponseDto {
    private int envioID;
    private String tracking;
    private EstadoEnvio estadoEnvio;
    private LocalDate fechaEntrega;
    private DomicilioResponseDto domicilio;
    private Integer pedidoID;
}
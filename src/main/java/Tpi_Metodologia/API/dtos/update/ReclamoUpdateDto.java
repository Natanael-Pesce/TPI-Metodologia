package Tpi_Metodologia.API.dtos.update;

import lombok.Data;

@Data
public class ReclamoUpdateDto {

    private String motivo;
    private String tipo;
    private String estado;
    private String respuesta;
}
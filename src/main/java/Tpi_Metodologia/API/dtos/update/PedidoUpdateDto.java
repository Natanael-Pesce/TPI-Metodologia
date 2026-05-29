package Tpi_Metodologia.API.dtos.update;

import lombok.Data;

@Data
public class PedidoUpdateDto {

    private String estado; // CONFIRMADO, EN_CAMINO, ENTREGADO, CANCELADO
}
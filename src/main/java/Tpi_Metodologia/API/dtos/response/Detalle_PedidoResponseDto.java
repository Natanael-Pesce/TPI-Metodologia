package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

@Data
public class Detalle_PedidoResponseDto {
    private int detallePedidoID;
    private int productoID;
    private String nombreProducto;
    private double precioUnitario;
    private int cantidad;
    private double subTotal;
}
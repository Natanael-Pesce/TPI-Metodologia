package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

@Data
public class ProductoResponseDto {

    private int productoID;
    private String nombreProducto;
    private double precioProducto;
    private String imagen;
    private int stock;
    private int stockMin;
    private boolean productoActivo;
    private CuponResponseDto cupon; // DTO, no entidad
}
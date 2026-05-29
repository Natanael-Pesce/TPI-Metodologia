package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * CORRECCIÓN:
 * private Producto productos → private Integer productoID.
 * Solo se necesita el ID del producto y la cantidad. El subTotal lo calcula el Service.
 */
@Data
public class Pedido_DetalleRegistroDto {

    @NotNull(message = "El producto es obligatorio")
    private Integer productoID;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    // subTotal NO va en el DTO de entrada - lo calcula el servidor
}
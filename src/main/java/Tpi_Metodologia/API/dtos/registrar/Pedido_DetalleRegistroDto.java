package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Pedido_DetalleRegistroDto {

    @NotNull(message = "El producto es obligatorio")
    private Integer productoID;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

}
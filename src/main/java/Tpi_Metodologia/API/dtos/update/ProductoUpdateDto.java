package Tpi_Metodologia.API.dtos.update;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductoUpdateDto {

    private String nombreProducto;

    @Positive(message = "El precio debe ser mayor a 0")
    private Double precioProducto;

    private String imagen;

    @Min(value = 0)
    private Integer stock;

    @Min(value = 0)
    private Integer stockMin;

    private Boolean productoActivo;
    private Integer cuponID;
}

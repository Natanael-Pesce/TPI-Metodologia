package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * CORRECCIÓN:
 * private Cupon cupon → private Integer cuponID.
 * No se pasa la entidad completa, solo el ID del cupón a asociar (opcional).
 */
@Data
public class ProductoRegistroDto {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @Positive(message = "El precio debe ser mayor a 0")
    private double precioProducto;

    private String imagen;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private int stockMin;

    @NotNull
    private boolean productoActivo;

    // CORREGIDO: solo el ID del cupón, no la entidad completa
    private Integer cuponID;
}
package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * NUEVO: DTO para crear un Kit.
 * Hereda los datos de Producto + lista de IDs de productos que lo componen.
 * Responde a la nota en notas.txt: en el frontend, si esKit=true se usa este endpoint.
 */
@Data
public class KitRegistroDto {

    @NotBlank(message = "El nombre del kit es obligatorio")
    private String nombreProducto;

    @Positive(message = "El precio debe ser mayor a 0")
    private double precioProducto;

    private String imagen;

    @Min(value = 0)
    private int stock;

    private int stockMin;
    private boolean productoActivo;
    private Integer cuponID;

    @NotEmpty(message = "Un kit debe tener al menos un producto")
    private List<Integer> productosIDs; // IDs de los Producto que componen el Kit
}
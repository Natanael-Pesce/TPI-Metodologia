package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReclamoRegistroDto {

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    private String tipo; // PRODUCTO_DEFECTUOSO, ENTREGA_TARDÍA, ERROR_COBRO , etc.

    @NotNull(message = "El pedido es obligatorio")
    private Integer pedidoID;

    @NotNull(message = "El cliente es obligatorio")
    private Integer usuarioID;

}
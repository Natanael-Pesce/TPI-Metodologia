package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRegistroDto {

    @NotNull(message = "El cliente es obligatorio")
    private Integer UsuarioID;

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    @Valid
    private List<Pedido_DetalleRegistroDto> detalles;

    @Valid
    private PagoRegistroDto pago;

    private Integer domicilioEnvioID;

    private String codigoCupon;
}
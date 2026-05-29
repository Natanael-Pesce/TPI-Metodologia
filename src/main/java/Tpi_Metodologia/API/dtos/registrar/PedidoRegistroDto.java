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

    // Datos de pago opcionales al crear (puede pagarse después)
    @Valid
    private PagoRegistroDto pago;

    // Domicilio de envío (ID de domicilio existente del cliente)
    private Integer domicilioEnvioID;

    // Código de cupón opcional
    private String codigoCupon;
}
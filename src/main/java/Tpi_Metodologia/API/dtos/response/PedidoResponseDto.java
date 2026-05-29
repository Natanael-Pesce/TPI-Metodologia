package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import Tpi_Metodologia.API.utility.EstadoPedido;

@Data
public class PedidoResponseDto {

    private int pedidoID;
    private int usuarioID;
    private String usuarioNombre;
    private String usuarioApellido;
    private List<Detalle_PedidoResponseDto> detalles;
    private double total;
    private EstadoPedido estado;
    private LocalDate fechaPedido;
    private PagoResponseDto pago;
    private EnvioResponseDto envio;
}
package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.response.PagoResponseDto;
import Tpi_Metodologia.API.utility.EstadoPago;

import java.util.List;

public interface IPagoService {

    PagoResponseDto obtenerPorId(int id);

    List<PagoResponseDto> listarTodos();

    List<PagoResponseDto> listarPorEstado(EstadoPago estado);

    /** Aprueba el pago y avanza el pedido a CONFIRMADO */
    PagoResponseDto aprobarPago(int pagoID);

    /** Rechaza el pago, devuelve stock y cancela el pedido */
    PagoResponseDto rechazarPago(int pagoID);
}

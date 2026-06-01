package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.response.PagoResponseDto;
import Tpi_Metodologia.API.utility.EstadoPago;

import java.util.List;

public interface IPagoService {

    PagoResponseDto obtenerPorId(int id);

    List<PagoResponseDto> listarTodos();

    List<PagoResponseDto> listarPorEstado(EstadoPago estado);

    PagoResponseDto aprobarPago(int pagoID);

    PagoResponseDto rechazarPago(int pagoID);
}

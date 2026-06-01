package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.update.PedidoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.PedidoRegistroDto;
import Tpi_Metodologia.API.dtos.response.PedidoResponseDto;

import java.util.List;

public interface IPedidoService {

    PedidoResponseDto crear(PedidoRegistroDto dto);

    PedidoResponseDto obtenerPorId(int id);

    List<PedidoResponseDto> listarTodos();

    List<PedidoResponseDto> listarPorCliente(int clienteID);

    List<PedidoResponseDto> listarPorEstado(String estado);

    PedidoResponseDto actualizarEstado(int id, PedidoUpdateDto dto);

    void cancelar(int id);

    PedidoResponseDto confirmarPedido(int pedidoID);
}
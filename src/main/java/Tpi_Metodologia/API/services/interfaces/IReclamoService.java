package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.registrar.ReclamoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ReclamoResponseDto;
import Tpi_Metodologia.API.dtos.update.ReclamoUpdateDto;

import java.util.List;

public interface IReclamoService {

    ReclamoResponseDto crear(ReclamoRegistroDto dto);

    ReclamoResponseDto obtenerPorId(int id);

    List<ReclamoResponseDto> listarTodos();

    List<ReclamoResponseDto> listarPorCliente(int clienteID);

    List<ReclamoResponseDto> listarPorEstado(String estado);

    ReclamoResponseDto cambiarEstado(int id, String nuevoEstado);

    ReclamoResponseDto update(int id, ReclamoUpdateDto dto);
}
package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.registrar.CuponRegistroDto;
import Tpi_Metodologia.API.dtos.response.CuponResponseDto;

import java.util.List;

public interface ICuponService {

    CuponResponseDto crear(CuponRegistroDto dto);

    CuponResponseDto obtenerPorId(int id);

    CuponResponseDto obtenerPorCodigo(String codigo);

    List<CuponResponseDto> listarTodos();

    List<CuponResponseDto> listarActivos();

    CuponResponseDto actualizar(int id, CuponRegistroDto dto);

    void eliminar(int id);
}
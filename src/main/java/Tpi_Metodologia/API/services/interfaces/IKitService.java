package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.registrar.KitRegistroDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;

import java.util.List;

public interface IKitService {

    ProductoResponseDto crear(KitRegistroDto dto);

    ProductoResponseDto obtenerPorId(int id);

    List<ProductoResponseDto> listarTodos();

    ProductoResponseDto actualizar(int id, KitRegistroDto dto);

    void eliminar(int id);
}
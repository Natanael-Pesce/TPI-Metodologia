package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.update.ProductoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.ProductoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;

import java.util.List;

public interface IProductoService {

    ProductoResponseDto crear(ProductoRegistroDto dto);

    ProductoResponseDto obtenerPorId(int id);

    List<ProductoResponseDto> listarTodos();

    List<ProductoResponseDto> listarActivos();

    List<ProductoResponseDto> buscarPorNombre(String nombre);

    List<ProductoResponseDto> listarConStockBajo();

    ProductoResponseDto actualizar(int id, ProductoUpdateDto dto);

    void eliminar(int id);

    // Activar/desactivar producto (baja lógica)
    ProductoResponseDto cambiarEstado(int id, boolean activo);
}
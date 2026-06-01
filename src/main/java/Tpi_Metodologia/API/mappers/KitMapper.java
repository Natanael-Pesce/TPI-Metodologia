package Tpi_Metodologia.API.mappers;

import java.util.stream.Collectors;

import Tpi_Metodologia.API.dtos.registrar.KitRegistroDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;
import Tpi_Metodologia.API.models.Cupon;
import Tpi_Metodologia.API.models.Kit;
import Tpi_Metodologia.API.models.Producto;
import Tpi_Metodologia.API.repositories.CuponRepository;
import Tpi_Metodologia.API.repositories.ProductoRepository;

import java.util.List;

public class KitMapper {
        public static Kit toEntity(KitRegistroDto dto,ProductoRepository productoRepository,CuponRepository cuponRepository) {
 
        Kit kit = new Kit();
 
        kit.setNombreProducto(dto.getNombreProducto());
        kit.setPrecioProducto(dto.getPrecioProducto());
        kit.setImagen(dto.getImagen());
        kit.setStock(dto.getStock());
        kit.setStockMin(dto.getStockMin());
        kit.setProductoActivo(dto.isProductoActivo());
 
        if (dto.getCuponID() != null) {
            Cupon cupon = cuponRepository.findById(dto.getCuponID())
                    .orElseThrow(() -> new RuntimeException("Cupón no encontrado con ID: " + dto.getCuponID()));
            kit.setCupon(cupon);
        }

        List<Producto> productos = dto.getProductosIDs().stream()
                .map(id -> productoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Producto componente no encontrado con ID: " + id)))
                .collect(Collectors.toList());
        kit.setProductos(productos);
 
        return kit;
    }
 
    public static ProductoResponseDto toDto(Kit kit) {
        return ProductoMapper.toDto(kit);
    }
}

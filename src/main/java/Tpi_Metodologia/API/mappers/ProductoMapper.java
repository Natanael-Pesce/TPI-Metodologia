package Tpi_Metodologia.API.mappers;

import Tpi_Metodologia.API.dtos.registrar.ProductoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;
import Tpi_Metodologia.API.models.Cupon;
import Tpi_Metodologia.API.models.Producto;
import Tpi_Metodologia.API.repositories.CuponRepository;

public class ProductoMapper {
        public static Producto toEntity(ProductoRegistroDto dto, CuponRepository cuponRepository) {
        Producto producto = new Producto();
        producto.setNombreProducto(dto.getNombreProducto());
        producto.setPrecioProducto(dto.getPrecioProducto());
        producto.setImagen(dto.getImagen());
        producto.setStock(dto.getStock());
        producto.setStockMin(dto.getStockMin());
        producto.setProductoActivo(dto.isProductoActivo());
 
        if (dto.getCuponID() != null) {
            Cupon cupon = cuponRepository.findById(dto.getCuponID())
                    .orElseThrow(() -> new RuntimeException("Cupón no encontrado con ID: " + dto.getCuponID()));
            producto.setCupon(cupon);
        }
 
        return producto;
    }
 
    public static ProductoResponseDto toDto(Producto producto) {
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setProductoID(producto.getProductoID());
        dto.setNombreProducto(producto.getNombreProducto());
        dto.setPrecioProducto(producto.getPrecioProducto());
        dto.setImagen(producto.getImagen());
        dto.setStock(producto.getStock());
        dto.setStockMin(producto.getStockMin());
        dto.setProductoActivo(producto.isProductoActivo());
 
        if (producto.getCupon() != null) {
            dto.setCupon(CuponMapper.toDto(producto.getCupon()));
        }
 
        return dto;
    }
}

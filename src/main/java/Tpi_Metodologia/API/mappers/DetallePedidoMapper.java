package Tpi_Metodologia.API.mappers;

import Tpi_Metodologia.API.dtos.registrar.Pedido_DetalleRegistroDto;
import Tpi_Metodologia.API.dtos.response.Detalle_PedidoResponseDto;
import Tpi_Metodologia.API.models.Detalle_Pedido;
import Tpi_Metodologia.API.models.Producto;
import Tpi_Metodologia.API.repositories.ProductoRepository;

public class DetallePedidoMapper {

        public static Detalle_Pedido toEntity(Pedido_DetalleRegistroDto dto, ProductoRepository productoRepository) {
        Detalle_Pedido detalle = new Detalle_Pedido();
 
        Producto producto = productoRepository.findById(dto.getProductoID())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoID()));
 
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());
        detalle.setSubTotal(producto.getPrecioProducto() * dto.getCantidad());
        // detalle.setPedido(...) → se asigna en PedidoMapper
 
        return detalle;
    }
 
    public static Detalle_PedidoResponseDto toDto(Detalle_Pedido detalle) {
        Detalle_PedidoResponseDto dto = new Detalle_PedidoResponseDto();
        dto.setDetallePedidoID(detalle.getDetallePedidoID());
        dto.setProductoID(detalle.getProducto().getProductoID());
        dto.setNombreProducto(detalle.getProducto().getNombreProducto());
        dto.setPrecioUnitario(detalle.getProducto().getPrecioProducto());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubTotal(detalle.getSubTotal());
        return dto;
    }
    
}

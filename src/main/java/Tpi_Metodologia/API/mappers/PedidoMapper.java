package Tpi_Metodologia.API.mappers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import Tpi_Metodologia.API.dtos.registrar.PedidoRegistroDto;
import Tpi_Metodologia.API.dtos.response.Detalle_PedidoResponseDto;
import Tpi_Metodologia.API.dtos.response.PedidoResponseDto;
import Tpi_Metodologia.API.models.Detalle_Pedido;
import Tpi_Metodologia.API.models.Envio;
import Tpi_Metodologia.API.models.Pago;
import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.models.Usuario;
import Tpi_Metodologia.API.repositories.DomicilioRepository;
import Tpi_Metodologia.API.repositories.ProductoRepository;
import Tpi_Metodologia.API.repositories.UsuarioRepository;
import Tpi_Metodologia.API.utility.EstadoPedido;

import java.util.List;

public class PedidoMapper {

        public static Pedido toEntity(PedidoRegistroDto dto,UsuarioRepository usuarioRepository,ProductoRepository productoRepository,DomicilioRepository domicilioRepository) {
 
        Pedido pedido = new Pedido();

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioID())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioID()));
        pedido.setUsuario(usuario);
 
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaPedido(LocalDate.now());

        List<Detalle_Pedido> detalles = new ArrayList<>();
        double total = 0;
 
        for (var detalleDto : dto.getDetalles()) {
            Detalle_Pedido detalle = DetallePedidoMapper.toEntity(detalleDto, productoRepository);
            detalle.setPedido(pedido);
            detalles.add(detalle);
            total += detalle.getSubTotal();
        }
 
        pedido.setDetalles(detalles);
        pedido.setTotal(total);
 
        if (dto.getPago() != null) {
            Pago pago = PagoMapper.toEntity(dto.getPago());
            pedido.setPago(pago);
        }
 
        if (dto.getDomicilioEnvioID() != null) {
            Envio envio = EnvioMapper.toEntity(dto.getDomicilioEnvioID(), domicilioRepository);
            pedido.setEnvio(envio);
        }
 
        return pedido;
    }

    public static PedidoResponseDto toDto(Pedido pedido) {
        PedidoResponseDto dto = new PedidoResponseDto();
        dto.setPedidoID(pedido.getPedidoID());
        dto.setUsuarioID(pedido.getUsuario().getUsuarioID());
        dto.setUsuarioNombre(pedido.getUsuario().getNombre());
        dto.setUsuarioApellido(pedido.getUsuario().getApellido());
        dto.setTotal(pedido.getTotal());
        dto.setEstado(pedido.getEstado());
        dto.setFechaPedido(pedido.getFechaPedido());
 
        if (pedido.getDetalles() != null) {
            List<Detalle_PedidoResponseDto> detallesDto = pedido.getDetalles().stream()
                    .map(DetallePedidoMapper::toDto)
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDto);
        }
 
        if (pedido.getPago() != null) {
            dto.setPago(PagoMapper.toDto(pedido.getPago()));
        }
 
        if (pedido.getEnvio() != null) {
            dto.setEnvio(EnvioMapper.toDto(pedido.getEnvio()));
        }
 
        return dto;
        }
}

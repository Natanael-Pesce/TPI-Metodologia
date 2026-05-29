package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.dtos.update.PedidoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.Pedido_DetalleRegistroDto;
import Tpi_Metodologia.API.dtos.response.*;
import Tpi_Metodologia.API.dtos.registrar.PedidoRegistroDto;
import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.models.*;
import Tpi_Metodologia.API.repositories.*;
import Tpi_Metodologia.API.services.interfaces.IPedidoService;
import Tpi_Metodologia.API.utility.EstadoEnvio;
import Tpi_Metodologia.API.utility.EstadoPago;
import Tpi_Metodologia.API.utility.EstadoPedido;
import Tpi_Metodologia.API.utility.TipoPago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;
    private final DomicilioRepository domicilioRepository;

    @Override
    @Transactional
    public PedidoResponseDto crear(PedidoRegistroDto dto) {
        // 1. Validar cliente
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioID())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioID()));

        if (dto.getDomicilioEnvioID() != null) {
            // Verificar que el domicilio pertenece al usuario
            boolean esDomicilioDelUsuario = usuario.getDomicilios()
            .stream()
            .anyMatch(d -> d.getDomicilioID() == dto.getDomicilioEnvioID());

            if (!esDomicilioDelUsuario) {
                throw new BadRequestException("El domicilio de envío no pertenece al usuario indicado");
            }
        }

        // 2. Construir detalles y validar stock
        List<Detalle_Pedido> detalles = new ArrayList<>();
        double total = 0;

        for (Pedido_DetalleRegistroDto detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoID())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detalleDto.getProductoID()));

            if (!producto.isProductoActivo()) {
                throw new BadRequestException("El producto '" + producto.getNombreProducto() + "' no está activo");
            }
            if (producto.getStock() < detalleDto.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para '" + producto.getNombreProducto()
                        + "'. Disponible: " + producto.getStock());
            }

            // Calcular subtotal
            double subTotal = producto.getPrecioProducto() * detalleDto.getCantidad();
            total += subTotal;

            // Descontar stock
            producto.setStock(producto.getStock() - detalleDto.getCantidad());
            productoRepository.save(producto);

            Detalle_Pedido detalle = new Detalle_Pedido();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setSubTotal(subTotal);
            detalles.add(detalle);
        }

        // 3. Aplicar descuento de cupón si corresponde
        if (dto.getCodigoCupon() != null && !dto.getCodigoCupon().isBlank()) {
            Cupon cupon = cuponRepository.findByCodigo(dto.getCodigoCupon().toUpperCase())
                    .orElseThrow(() -> new BadRequestException("Cupón '" + dto.getCodigoCupon() + "' no encontrado"));

            LocalDate hoy = LocalDate.now();
            if (!cupon.isEstado() || hoy.isBefore(cupon.getFechaInicio()) || hoy.isAfter(cupon.getFechaFin())) {
                throw new BadRequestException("El cupón no es válido o está vencido");
            }

            total = total * (1 - cupon.getDescuento() / 100.0);
        }

        // 4. Armar el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(total);

        // Asignar detalles al pedido (relación bidireccional)
        for (Detalle_Pedido d : detalles) {
            d.setPedido(pedido);
        }
        pedido.setDetalles(detalles);

        // 5. Pago opcional
        if (dto.getPago() != null) {
            Pago pago = new Pago();
            //pago.setTipoPago(dto.getPago().getTipoPago());
            pago.setTipoPago(TipoPago.valueOf(dto.getPago().getTipoPago().toUpperCase()));
            pago.setMonto(total);
            pago.setEstadoPago(EstadoPago.PENDIENTE);
            pago.setFechaPago(LocalDate.now());
            pedido.setPago(pago);
        }

        // 6. Envío opcional
        if (dto.getDomicilioEnvioID() != null) {
            Domicilio domicilio = domicilioRepository.findById(dto.getDomicilioEnvioID())
                    .orElseThrow(() -> new ResourceNotFoundException("Domicilio", dto.getDomicilioEnvioID()));
            Envio envio = new Envio();
            envio.setDomicilio(domicilio);
            envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);
            pedido.setEnvio(envio);
        }

        return toResponseDto(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponseDto obtenerPorId(int id) {
        return toResponseDto(obtenerPedidoOException(id));
    }

    @Override
    public List<PedidoResponseDto> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDto> listarPorCliente(int UsuarioID) {
        if (!usuarioRepository.existsById(UsuarioID)) {
            throw new ResourceNotFoundException("Usuario", UsuarioID);
        }
        return pedidoRepository.findByUsuarioUsuarioID(UsuarioID).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDto> listarPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponseDto actualizarEstado(int id, PedidoUpdateDto dto) {
        Pedido pedido = obtenerPedidoOException(id);
        if (dto.getEstado() != null) {
            //pedido.setEstado(dto.getEstado().toUpperCase());
            pedido.setEstado(EstadoPedido.valueOf(dto.getEstado().toUpperCase()));
        }
        return toResponseDto(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public void cancelar(int id) {
        Pedido pedido = obtenerPedidoOException(id);
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new BadRequestException("No se puede cancelar un pedido ya entregado");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new BadRequestException("El pedido ya está cancelado");
        }

        // Devolver stock al cancelar
        if (pedido.getDetalles() != null) {
            for (Detalle_Pedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    private Pedido obtenerPedidoOException(int id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    private PedidoResponseDto toResponseDto(Pedido p) {
        PedidoResponseDto dto = new PedidoResponseDto();
        dto.setPedidoID(p.getPedidoID());
        if (p.getUsuario() != null) {
            dto.setUsuarioID(p.getUsuario().getUsuarioID());
            dto.setUsuarioNombre(p.getUsuario().getNombre() + " " + p.getUsuario().getApellido());
        }
        dto.setTotal(p.getTotal());
        dto.setEstado(p.getEstado());
        dto.setFechaPedido(p.getFechaPedido());

        if (p.getDetalles() != null) {
            dto.setDetalles(p.getDetalles().stream()
                    .map(this::toDetalleResponseDto)
                    .collect(Collectors.toList()));
        }

        if (p.getPago() != null) {
            dto.setPago(toPagoResponseDto(p.getPago()));
        }

        if (p.getEnvio() != null) {
            dto.setEnvio(toEnvioResponseDto(p.getEnvio()));
        }

        return dto;
    }

    private Detalle_PedidoResponseDto toDetalleResponseDto(Detalle_Pedido d) {
        Detalle_PedidoResponseDto dto = new Detalle_PedidoResponseDto();
        dto.setDetallePedidoID(d.getDetallePedidoID());
        dto.setCantidad(d.getCantidad());
        dto.setSubTotal(d.getSubTotal());
        if (d.getProducto() != null) {
            dto.setProductoID(d.getProducto().getProductoID());
            dto.setNombreProducto(d.getProducto().getNombreProducto());
            dto.setPrecioUnitario(d.getProducto().getPrecioProducto());
        }
        return dto;
    }

    private PagoResponseDto toPagoResponseDto(Pago p) {
        PagoResponseDto dto = new PagoResponseDto();
        dto.setPagoID(p.getPagoID());
        dto.setTipoPago(p.getTipoPago());
        dto.setEstadoPago(p.getEstadoPago());
        dto.setFechaPago(p.getFechaPago());
        dto.setMonto(p.getMonto());
        return dto;
    }

    private EnvioResponseDto toEnvioResponseDto(Envio e) {
        EnvioResponseDto dto = new EnvioResponseDto();
        dto.setEnvioID(e.getEnvioID());
        dto.setTracking(e.getTracking());
        dto.setEstadoEnvio(e.getEstadoEnvio());
        dto.setFechaEntrega(e.getFechaEntrega());
        if (e.getDomicilio() != null) {
            DomicilioResponseDto dom = new DomicilioResponseDto();
            dom.setDomicilioID(e.getDomicilio().getDomicilioID());
            dom.setCiudad(e.getDomicilio().getCiudad());
            dom.setCalle(e.getDomicilio().getCalle());
            dom.setNro(e.getDomicilio().getNro());
            dom.setPais(e.getDomicilio().getPais());
            dto.setDomicilio(dom);
        }
        return dto;
    }
    public PedidoResponseDto confirmarPedido(int pedidoID) {
    
        Pedido pedido = obtenerPedidoOException(pedidoID);

    // 2. Validar que está en estado PENDIENTE
    if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
        throw new BadRequestException(
            "Solo se puede confirmar un pedido PENDIENTE. Estado actual: "
            + pedido.getEstado());
    }

    // 3. Re-validar stock (puede haber cambiado desde que se creó el pedido)
    if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
        throw new BadRequestException("El pedido no tiene productos");
    }
    for (Detalle_Pedido detalle : pedido.getDetalles()) {
        Producto producto = detalle.getProducto();
        // Stock ya fue descontado al crear — solo verificamos que no sea negativo
        if (producto.getStock() < 0) {
            throw new BadRequestException(
                "Stock inconsistente para: " + producto.getNombreProducto());
        }
    }

    // 4. Confirmar el pago si existe
    if (pedido.getPago() != null) {
        Pago pago = pedido.getPago();
        pago.setEstadoPago(EstadoPago.APROBADO);
        pago.setFechaPago(LocalDate.now());
    }

    // 5. Asignar tracking al envío si existe
    if (pedido.getEnvio() != null) {
        Envio envio = pedido.getEnvio();
        if (envio.getTracking() == null || envio.getTracking().isBlank()) {
            // Generar código de tracking único
            String tracking = "TRK-" + pedidoID + "-"
                + LocalDate.now().getYear()
                + "-" + String.format("%04d", pedidoID);
            envio.setTracking(tracking);
        }
        envio.setEstadoEnvio(EstadoEnvio.PREPARANDO);
                // Fecha estimada: 5 días hábiles
        envio.setFechaEntrega(LocalDate.now().plusDays(5));
    }

    // 6. Cambiar estado del pedido
    pedido.setEstado(EstadoPedido.ENTREGADO);

    // 7. Guardar todo en una única transacción (cascade se encarga de Pago y Envio)
    return toResponseDto(pedidoRepository.save(pedido));
}
//Revisar

}
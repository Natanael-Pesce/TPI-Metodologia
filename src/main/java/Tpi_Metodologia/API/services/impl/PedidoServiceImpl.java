package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.dtos.update.PedidoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.Pedido_DetalleRegistroDto;
import Tpi_Metodologia.API.dtos.response.*;
import Tpi_Metodologia.API.dtos.registrar.PedidoRegistroDto;
import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.models.*;
import Tpi_Metodologia.API.repositories.*;
import Tpi_Metodologia.API.services.interfaces.IEmailService;
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

/**
 * ARCHIVO MODIFICADO: src/main/java/Tpi_Metodologia/API/services/impl/PedidoServiceImpl.java
 *
 * CAMBIOS RESPECTO AL ORIGINAL:
 *   1. Inyección de IEmailService (HU-01, HU-02, HU-03)
 *   2. crear() → envía email de confirmación de compra (HU-01)
 *   3. confirmarPedido() → BUG FIX: estado cambia a CONFIRMADO (no ENTREGADO)
 *   4. confirmarPedido() → envía email con tracking (HU-02)
 *   5. actualizarEstado() → envía email de cambio de estado (HU-03)
 *   6. PedidoRepository.findByEstado() ahora recibe EstadoPedido (enum) — ver repo
 */
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;
    private final DomicilioRepository domicilioRepository;
    private final IEmailService emailService; // ← NUEVO: inyección para notificaciones

    @Override
    @Transactional
    public PedidoResponseDto crear(PedidoRegistroDto dto) {
        // 1. Validar cliente
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioID())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioID()));

        if (dto.getDomicilioEnvioID() != null) {
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

            double subTotal = producto.getPrecioProducto() * detalleDto.getCantidad();
            total += subTotal;

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

        for (Detalle_Pedido d : detalles) {
            d.setPedido(pedido);
        }
        pedido.setDetalles(detalles);

        // 5. Pago opcional
        if (dto.getPago() != null) {
            Pago pago = new Pago();
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

        PedidoResponseDto response = toResponseDto(pedidoRepository.save(pedido));

        // HU-01: Email de confirmación de compra
        emailService.enviarConfirmacionPedido(
            usuario.getCorreo(),
            usuario.getNombre() + " " + usuario.getApellido(),
            response.getPedidoID(),
            response.getTotal()
        );

        return response;
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
    public List<PedidoResponseDto> listarPorCliente(int usuarioID) {
        if (!usuarioRepository.existsById(usuarioID)) {
            throw new ResourceNotFoundException("Usuario", usuarioID);
        }
        return pedidoRepository.findByUsuarioUsuarioID(usuarioID).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDto> listarPorEstado(String estado) {
        // FIX: convertir String a enum para que coincida con la entidad
        EstadoPedido estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
        return pedidoRepository.findByEstado(estadoEnum).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponseDto actualizarEstado(int id, PedidoUpdateDto dto) {
        Pedido pedido = obtenerPedidoOException(id);
        if (dto.getEstado() != null) {
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(dto.getEstado().toUpperCase());
            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);

            // HU-03: Notificar al cliente el cambio de estado
            emailService.enviarNotificacionEstadoPedido(
                pedido.getUsuario().getCorreo(),
                pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
                pedido.getPedidoID(),
                nuevoEstado
            );
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

        if (pedido.getDetalles() != null) {
            for (Detalle_Pedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        // HU-03: Notificar cancelación
        emailService.enviarNotificacionEstadoPedido(
            pedido.getUsuario().getCorreo(),
            pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
            pedido.getPedidoID(),
            EstadoPedido.CANCELADO
        );
    }

    @Override
    @Transactional
    public PedidoResponseDto confirmarPedido(int pedidoID) {
        Pedido pedido = obtenerPedidoOException(pedidoID);

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BadRequestException(
                "Solo se puede confirmar un pedido PENDIENTE. Estado actual: " + pedido.getEstado());
        }

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new BadRequestException("El pedido no tiene productos");
        }
        for (Detalle_Pedido detalle : pedido.getDetalles()) {
            if (detalle.getProducto().getStock() < 0) {
                throw new BadRequestException("Stock inconsistente para: " + detalle.getProducto().getNombreProducto());
            }
        }

        // Confirmar el pago si existe
        if (pedido.getPago() != null) {
            pedido.getPago().setEstadoPago(EstadoPago.APROBADO);
            pedido.getPago().setFechaPago(LocalDate.now());
        }

        // Asignar tracking al envío si existe
        String trackingGenerado = null;
        if (pedido.getEnvio() != null) {
            Envio envio = pedido.getEnvio();
            if (envio.getTracking() == null || envio.getTracking().isBlank()) {
                trackingGenerado = "TRK-" + pedidoID + "-"
                    + LocalDate.now().getYear()
                    + "-" + String.format("%04d", pedidoID);
                envio.setTracking(trackingGenerado);
            } else {
                trackingGenerado = envio.getTracking();
            }
            envio.setEstadoEnvio(EstadoEnvio.PREPARANDO);
            envio.setFechaEntrega(LocalDate.now().plusDays(5));
        }

        // BUG FIX: era ENTREGADO — debe ser CONFIRMADO
        pedido.setEstado(EstadoPedido.CONFIRMADO);

        PedidoResponseDto response = toResponseDto(pedidoRepository.save(pedido));

        // HU-02: Email con número de tracking
        if (trackingGenerado != null) {
            emailService.enviarNumeroTracking(
                pedido.getUsuario().getCorreo(),
                pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
                pedidoID,
                trackingGenerado
            );
        }

        // HU-03: Notificar estado CONFIRMADO
        emailService.enviarNotificacionEstadoPedido(
            pedido.getUsuario().getCorreo(),
            pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
            pedidoID,
            EstadoPedido.CONFIRMADO
        );

        return response;
    }

    // ─────────────────────────────────────────────────────────────
    // Auxiliares de mapeo
    // ─────────────────────────────────────────────────────────────

    private Pedido obtenerPedidoOException(int id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    private PedidoResponseDto toResponseDto(Pedido p) {
        PedidoResponseDto dto = new PedidoResponseDto();
        dto.setPedidoID(p.getPedidoID());
        if (p.getUsuario() != null) {
            dto.setUsuarioID(p.getUsuario().getUsuarioID());
            dto.setUsuarioNombre(p.getUsuario().getNombre());
            dto.setUsuarioApellido(p.getUsuario().getApellido());
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
        dto.setFechaEntrega(e.getFechaEntrega()); // FIX: antes usaba nombre de campo inexistente
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
}
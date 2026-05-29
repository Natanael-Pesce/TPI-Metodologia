package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.dtos.response.PagoResponseDto;
import Tpi_Metodologia.API.models.Pago;
import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.models.Detalle_Pedido;
import Tpi_Metodologia.API.models.Producto;
import Tpi_Metodologia.API.repositories.PagoRepository;
import Tpi_Metodologia.API.repositories.ProductoRepository;
import Tpi_Metodologia.API.services.interfaces.IPagoService;
import Tpi_Metodologia.API.utility.EstadoPedido;
import Tpi_Metodologia.API.utility.EstadoPago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository pagoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public PagoResponseDto obtenerPorId(int id) {
        return toDto(obtenerOException(id));
    }

    @Override
    public List<PagoResponseDto> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PagoResponseDto> listarPorEstado(EstadoPago estado) {
        return pagoRepository.findByEstadoPago(estado).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PagoResponseDto aprobarPago(int pagoID) {
        Pago pago = obtenerOException(pagoID);

        if (pago.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new BadRequestException(
                "Solo se puede aprobar un pago PENDIENTE. Estado actual: "
                + pago.getEstadoPago());
        }

        pago.setEstadoPago(EstadoPago.APROBADO);
        pago.setFechaPago(LocalDate.now());

        // Avanzar el pedido asociado a CONFIRMADO
        if (pago.getPedido() != null) {
            pago.getPedido().setEstado(EstadoPedido.CONFIRMADO);
        }

        return toDto(pagoRepository.save(pago));
    }

    @Override
    @Transactional
    public PagoResponseDto rechazarPago(int pagoID) {
        Pago pago = obtenerOException(pagoID);

        if (pago.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new BadRequestException(
                "Solo se puede rechazar un pago PENDIENTE. Estado actual: "
                + pago.getEstadoPago());
        }

        pago.setEstadoPago(EstadoPago.RECHAZADO);

        // Devolver stock al rechazar el pago
        Pedido pedido = pago.getPedido();
        if (pedido != null && pedido.getDetalles() != null) {
            for (Detalle_Pedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
            pedido.setEstado(EstadoPedido.CANCELADO);
        }

        return toDto(pagoRepository.save(pago));
    }

    private Pago obtenerOException(int id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
    }

    private PagoResponseDto toDto(Pago p) {
        PagoResponseDto dto = new PagoResponseDto();
        dto.setPagoID(p.getPagoID());
        dto.setTipoPago(p.getTipoPago() != null ? p.getTipoPago() : null);
        dto.setEstadoPago(p.getEstadoPago() != null ? p.getEstadoPago() : null);
        dto.setFechaPago(p.getFechaPago());
        dto.setMonto(p.getMonto());
        return dto;
    }
}

package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.response.EnvioResponseDto;
import Tpi_Metodologia.API.dtos.update.EnvioUpdateDto;
import Tpi_Metodologia.API.models.Domicilio;
import Tpi_Metodologia.API.models.Envio;
import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.repositories.DomicilioRepository;
import Tpi_Metodologia.API.repositories.EnvioRepository;
import Tpi_Metodologia.API.services.interfaces.IEmailService;
import Tpi_Metodologia.API.services.interfaces.IEnvioService;
import Tpi_Metodologia.API.utility.EstadoEnvio;
import Tpi_Metodologia.API.utility.EstadoPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnvioServiceImpl implements IEnvioService {

    private final EnvioRepository envioRepository;
    private final DomicilioRepository domicilioRepository;
    private final IEmailService emailService; // ← NUEVO para HU-03

    @Override
    public EnvioResponseDto obtenerPorId(int id) {
        return toDto(obtenerOException(id));
    }

    @Override
    public EnvioResponseDto obtenerPorTracking(String tracking) {
        Envio envio = envioRepository.findByTracking(tracking)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Envio con tracking " + tracking, 0));
        return toDto(envio);
    }

    @Override
    public List<EnvioResponseDto> listarTodos() {
        return envioRepository.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EnvioResponseDto> listarPorEstado(EstadoEnvio estado) {
        return envioRepository.findByEstadoEnvio(estado).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnvioResponseDto actualizarEstado(int envioID, EstadoEnvio nuevoEstado, String tracking) {
        Envio envio = obtenerOException(envioID);
        validarTransicion(envio.getEstadoEnvio(), nuevoEstado);

        envio.setEstadoEnvio(nuevoEstado);

        if (tracking != null && !tracking.isBlank()) {
            envio.setTracking(tracking);
        }

        if (nuevoEstado == EstadoEnvio.ENTREGADO) {
            Pedido pedido = envio.getPedido();
            if (pedido != null) {
                pedido.setEstado(EstadoPedido.ENTREGADO);
            }
        }

        Envio guardado = envioRepository.save(envio);

        // HU-03: Notificar al cliente el cambio de estado del envío
        notificarCambioEstadoEnvio(guardado, nuevoEstado);

        return toDto(guardado);
    }

    @Override
    @Transactional
    public EnvioResponseDto update(int envioID, EnvioUpdateDto dto) {
        Envio envio = obtenerOException(envioID);
        EstadoEnvio estadoAnterior = envio.getEstadoEnvio();

        // Actualizar estado (con validación de transición)
        if (dto.getEstadoEnvio() != null && !dto.getEstadoEnvio().isBlank()) {
            EstadoEnvio nuevoEstado;
            try {
                nuevoEstado = EstadoEnvio.valueOf(dto.getEstadoEnvio().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Estado de envío inválido: " + dto.getEstadoEnvio()
                    + ". Valores válidos: PENDIENTE, PREPARANDO, DESPACHADO, EN_CAMINO, ENTREGADO, DEVUELTO");
            }
            validarTransicion(envio.getEstadoEnvio(), nuevoEstado);
            envio.setEstadoEnvio(nuevoEstado);

            // Propagar ENTREGADO al pedido
            if (nuevoEstado == EstadoEnvio.ENTREGADO && envio.getPedido() != null) {
                envio.getPedido().setEstado(EstadoPedido.ENTREGADO);
            }
        }

        // Actualizar tracking
        if (dto.getTracking() != null && !dto.getTracking().isBlank()) {
            envio.setTracking(dto.getTracking());
        }

        // Actualizar fecha de entrega
        if (dto.getFechaEntrega() != null) {
            envio.setFechaEntrega(dto.getFechaEntrega());
        }

        // Actualizar domicilio (solo si no fue despachado)
        if (dto.getDomicilioID() != null) {
            if (envio.getEstadoEnvio() == EstadoEnvio.DESPACHADO
                    || envio.getEstadoEnvio() == EstadoEnvio.EN_CAMINO
                    || envio.getEstadoEnvio() == EstadoEnvio.ENTREGADO) {
                throw new BadRequestException(
                    "No se puede cambiar el domicilio de un envío en estado " + envio.getEstadoEnvio());
            }
            Domicilio domicilio = domicilioRepository.findById(dto.getDomicilioID())
                    .orElseThrow(() -> new ResourceNotFoundException("Domicilio", dto.getDomicilioID()));
            envio.setDomicilio(domicilio);
        }

        Envio guardado = envioRepository.save(envio);

        // HU-03: Notificar solo si el estado cambió
        if (dto.getEstadoEnvio() != null && guardado.getEstadoEnvio() != estadoAnterior) {
            notificarCambioEstadoEnvio(guardado, guardado.getEstadoEnvio());
        }

        return toDto(guardado);
    }


    private void validarTransicion(EstadoEnvio actual, EstadoEnvio nuevo) {
        if (actual == EstadoEnvio.ENTREGADO || actual == EstadoEnvio.DEVUELTO) {
            throw new BadRequestException(
                "No se puede modificar un envío en estado " + actual);
        }
        if (actual.ordinal() > nuevo.ordinal()) {
            throw new BadRequestException(
                "Transición inválida: " + actual + " → " + nuevo);
        }
    }

    private void notificarCambioEstadoEnvio(Envio envio, EstadoEnvio nuevoEstado) {
        Pedido pedido = envio.getPedido();
        if (pedido != null && pedido.getUsuario() != null) {
            emailService.enviarNotificacionEstadoEnvio(
                pedido.getUsuario().getCorreo(),
                pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
                pedido.getPedidoID(),
                envio.getTracking(),
                nuevoEstado
            );
        }
    }

    private Envio obtenerOException(int id) {
        return envioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Envio", id));
    }

    private EnvioResponseDto toDto(Envio e) {
        EnvioResponseDto dto = new EnvioResponseDto();
        dto.setEnvioID(e.getEnvioID());
        dto.setTracking(e.getTracking());
        dto.setEstadoEnvio(e.getEstadoEnvio());
        dto.setFechaEntrega(e.getFechaEntrega());
        if (e.getDomicilio() != null) {
            DomicilioResponseDto dom = new DomicilioResponseDto();
            dom.setDomicilioID(e.getDomicilio().getDomicilioID());
            dom.setPais(e.getDomicilio().getPais());
            dom.setProvincia(e.getDomicilio().getProvincia());
            dom.setCiudad(e.getDomicilio().getCiudad());
            dom.setCalle(e.getDomicilio().getCalle());
            dom.setNro(e.getDomicilio().getNro());
            dom.setDepartamento(e.getDomicilio().getDepartamento());
            dom.setNroDepartamento(e.getDomicilio().getNroDepartamento());
            dom.setPiso(e.getDomicilio().getPiso());
            dto.setDomicilio(dom);
        }
        if (e.getPedido() != null) {
            dto.setPedidoID(e.getPedido().getPedidoID());
        }
        return dto;
    }
}

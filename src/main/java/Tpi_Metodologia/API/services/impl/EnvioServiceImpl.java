package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.response.EnvioResponseDto;
import Tpi_Metodologia.API.models.Envio;
import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.repositories.EnvioRepository;
import Tpi_Metodologia.API.services.interfaces.IEnvioService;
import Tpi_Metodologia.API.utility.EstadoPedido;
import Tpi_Metodologia.API.utility.EstadoEnvio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnvioServiceImpl implements IEnvioService {

    private final EnvioRepository envioRepository;

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
    public EnvioResponseDto actualizarEstado(int envioID, EstadoEnvio nuevoEstado,String tracking) {
        Envio envio = obtenerOException(envioID);

        validarTransicion(envio.getEstadoEnvio(), nuevoEstado);

        envio.setEstadoEnvio(nuevoEstado);

        if (tracking != null && !tracking.isBlank()) {
            envio.setTracking(tracking);
        }
            //Revisar
        if (nuevoEstado == EstadoEnvio.ENTREGADO) {
            Pedido pedido = envio.getPedido();
            if (pedido != null) {
                pedido.setEstado(EstadoPedido.ENTREGADO);
            }
        }

        return toDto(envioRepository.save(envio));
    }

    private void validarTransicion(EstadoEnvio actual, EstadoEnvio nuevo) {
        // Solo se permite avanzar, no retroceder
        if (actual == EstadoEnvio.ENTREGADO || actual == EstadoEnvio.DEVUELTO) {
            throw new BadRequestException(
                "No se puede modificar un envío en estado " + actual);
        }
        if (actual.ordinal() > nuevo.ordinal()) {
            throw new BadRequestException(
                "Transición inválida: " + actual + " → " + nuevo);
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
        dto.setEstadoEnvio(e.getEstadoEnvio() != null ? e.getEstadoEnvio() : null);
        //dto.setFechaEntrega(e.getFechaEstimadaEntrega());
        if (e.getDomicilio() != null) {
            DomicilioResponseDto dom = new DomicilioResponseDto();
            dom.setDomicilioID(e.getDomicilio().getDomicilioID());
            dom.setPais(e.getDomicilio().getPais());
            dom.setCiudad(e.getDomicilio().getCiudad());
            dom.setCalle(e.getDomicilio().getCalle());
            dom.setNro(e.getDomicilio().getNro());
            dto.setDomicilio(dom);
        }

            if (e.getPedido() != null) {
            dto.setPedidoID(e.getPedido().getPedidoID());
        }
        return dto;
    }
}


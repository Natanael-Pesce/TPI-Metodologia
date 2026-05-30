package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.dtos.registrar.DomicilioRegistroDto;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.update.DomicilioUpdateDto;
import Tpi_Metodologia.API.models.Domicilio;
import Tpi_Metodologia.API.repositories.DomicilioRepository;
import Tpi_Metodologia.API.services.interfaces.IDomicilioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * NUEVO: Implementación del servicio de Domicilio con CRUD completo.
 * Antes la gestión de domicilios solo existía dentro de UsuarioServiceImpl.
 *
 * ARCHIVO: src/main/java/Tpi_Metodologia/API/services/impl/DomicilioServiceImpl.java
 */
@Service
@RequiredArgsConstructor
public class DomicilioServiceImpl implements IDomicilioService {

    private final DomicilioRepository domicilioRepository;

    @Override
    @Transactional
    public DomicilioResponseDto crear(DomicilioRegistroDto dto) {
        Domicilio domicilio = toEntity(dto);
        return toResponseDto(domicilioRepository.save(domicilio));
    }

    @Override
    public DomicilioResponseDto obtenerPorId(int id) {
        return toResponseDto(obtenerOException(id));
    }

    @Override
    public List<DomicilioResponseDto> listarTodos() {
        return domicilioRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Actualización parcial: solo actualiza los campos no nulos del DTO.
     */
    @Override
    @Transactional
    public DomicilioResponseDto update(int id, DomicilioUpdateDto dto) {
        Domicilio domicilio = obtenerOException(id);

        if (dto.getPais() != null && !dto.getPais().isBlank()) {
            domicilio.setPais(dto.getPais());
        }
        if (dto.getProvincia() != null) {
            domicilio.setProvincia(dto.getProvincia());
        }
        if (dto.getCiudad() != null && !dto.getCiudad().isBlank()) {
            domicilio.setCiudad(dto.getCiudad());
        }
        if (dto.getCalle() != null && !dto.getCalle().isBlank()) {
            domicilio.setCalle(dto.getCalle());
        }
        if (dto.getNro() != null) {
            domicilio.setNro(dto.getNro());
        }
        if (dto.getDepartamento() != null) {
            domicilio.setDepartamento(dto.getDepartamento());
        }
        if (dto.getNroDepartamento() != null) {
            domicilio.setNroDepartamento(dto.getNroDepartamento());
        }
        if (dto.getPiso() != null) {
            domicilio.setPiso(dto.getPiso());
        }

        return toResponseDto(domicilioRepository.save(domicilio));
    }

    @Override
    public void eliminar(int id) {
        if (!domicilioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Domicilio", id);
        }
        domicilioRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────────
    // Auxiliares
    // ─────────────────────────────────────────────────────────────

    private Domicilio obtenerOException(int id) {
        return domicilioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domicilio", id));
    }

    private Domicilio toEntity(DomicilioRegistroDto dto) {
        Domicilio d = new Domicilio();
        d.setPais(dto.getPais());
        d.setProvincia(dto.getProvincia());
        d.setCiudad(dto.getCiudad());
        d.setCalle(dto.getCalle());
        d.setNro(dto.getNro());
        d.setDepartamento(dto.getDepartamento());
        d.setNroDepartamento(dto.getNroDepartamento());
        d.setPiso(dto.getPiso());
        return d;
    }

    public DomicilioResponseDto toResponseDto(Domicilio d) {
        DomicilioResponseDto dto = new DomicilioResponseDto();
        dto.setDomicilioID(d.getDomicilioID());
        dto.setPais(d.getPais());
        dto.setProvincia(d.getProvincia());
        dto.setCiudad(d.getCiudad());
        dto.setCalle(d.getCalle());
        dto.setNro(d.getNro());
        dto.setDepartamento(d.getDepartamento());
        dto.setNroDepartamento(d.getNroDepartamento());
        dto.setPiso(d.getPiso());
        return dto;
    }
}
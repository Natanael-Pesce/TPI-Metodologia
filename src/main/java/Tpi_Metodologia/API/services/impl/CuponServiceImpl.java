package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.dtos.registrar.CuponRegistroDto;
import Tpi_Metodologia.API.dtos.response.CuponResponseDto;
import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.models.Cupon;
import Tpi_Metodologia.API.repositories.CuponRepository;
import Tpi_Metodologia.API.services.interfaces.ICuponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuponServiceImpl implements ICuponService {

    private final CuponRepository cuponRepository;

    @Override
    @Transactional
    public CuponResponseDto crear(CuponRegistroDto dto) {
        if (cuponRepository.existsByCodigo(dto.getCodigo())) {
            throw new BadRequestException("Ya existe un cupón con el código '" + dto.getCodigo() + "'");
        }
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        Cupon cupon = new Cupon();
        cupon.setCodigo(dto.getCodigo().toUpperCase());
        cupon.setDescuento(dto.getDescuento());
        cupon.setFechaInicio(dto.getFechaInicio());
        cupon.setFechaFin(dto.getFechaFin());
        // Estado activo si las fechas son válidas
        cupon.setEstado(!LocalDate.now().isAfter(dto.getFechaFin()));

        return toResponseDto(cuponRepository.save(cupon));
    }

    @Override
    public CuponResponseDto obtenerPorId(int id) {
        return toResponseDto(obtenerCuponOException(id));
    }

    @Override
    public CuponResponseDto obtenerPorCodigo(String codigo) {
        Cupon cupon = cuponRepository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Cupón con código '" + codigo + "' no encontrado"));
        return toResponseDto(cupon);
    }

    @Override
    public List<CuponResponseDto> listarTodos() {
        return cuponRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuponResponseDto> listarActivos() {
        LocalDate hoy = LocalDate.now();
        return cuponRepository.findAll().stream()
                .filter(c -> c.isEstado()
                        && !hoy.isBefore(c.getFechaInicio())
                        && !hoy.isAfter(c.getFechaFin()))
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuponResponseDto actualizar(int id, CuponRegistroDto dto) {
        Cupon cupon = obtenerCuponOException(id);

        if (!cupon.getCodigo().equals(dto.getCodigo().toUpperCase())
                && cuponRepository.existsByCodigo(dto.getCodigo().toUpperCase())) {
            throw new BadRequestException("Ya existe un cupón con el código '" + dto.getCodigo() + "'");
        }
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        cupon.setCodigo(dto.getCodigo().toUpperCase());
        cupon.setDescuento(dto.getDescuento());
        cupon.setFechaInicio(dto.getFechaInicio());
        cupon.setFechaFin(dto.getFechaFin());
        cupon.setEstado(!LocalDate.now().isAfter(dto.getFechaFin()));

        return toResponseDto(cuponRepository.save(cupon));
    }

    @Override
    public void eliminar(int id) {
        if (!cuponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cupon", id);
        }
        cuponRepository.deleteById(id);
    }

    private Cupon obtenerCuponOException(int id) {
        return cuponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon", id));
    }

    public CuponResponseDto toResponseDto(Cupon c) {
        CuponResponseDto dto = new CuponResponseDto();
        dto.setCuponID(c.getCuponID());
        dto.setCodigo(c.getCodigo());
        dto.setDescuento(c.getDescuento());
        dto.setEstado(c.isEstado());
        dto.setFechaInicio(c.getFechaInicio());
        dto.setFechaFin(c.getFechaFin());
        return dto;
    }
}
package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.dtos.registrar.ReclamoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ReclamoResponseDto;
import Tpi_Metodologia.API.dtos.update.ReclamoUpdateDto;
import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.models.Reclamo;
import Tpi_Metodologia.API.models.Usuario;
import Tpi_Metodologia.API.repositories.PedidoRepository;
import Tpi_Metodologia.API.repositories.ReclamoRepository;
import Tpi_Metodologia.API.services.interfaces.IReclamoService;
import Tpi_Metodologia.API.utility.EstadoPedido;
import Tpi_Metodologia.API.utility.EstadoReclamo;
import Tpi_Metodologia.API.utility.Rol;
import Tpi_Metodologia.API.repositories.UsuarioRepository;
import Tpi_Metodologia.API.security.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReclamoServiceImpl implements IReclamoService {

    private final ReclamoRepository reclamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ADMIN')")
    public ReclamoResponseDto crear(ReclamoRegistroDto dto) {
        Usuario autenticado = SecurityUtils.getUsuarioAutenticado();
        
        if (autenticado.getRol() == Rol.ROLE_CLIENTE && autenticado.getUsuarioID() != dto.getUsuarioID()){
            throw new AccessDeniedException("No puedes crear reclamos para otros usuarios");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioID())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioID()));

        Pedido pedido = pedidoRepository.findById(dto.getPedidoID())
            .orElseThrow(() -> new ResourceNotFoundException("Pedido", dto.getPedidoID()));

        if (pedido.getUsuario().getUsuarioID() != dto.getUsuarioID()) {
            throw new BadRequestException("El pedido no pertenece al cliente indicado");
        }

        if (pedido.getEstado() == EstadoPedido.CANCELADO || pedido.getEstado() == EstadoPedido.PENDIENTE) {
            throw new BadRequestException("No se puede reclamar un pedido en estado " + pedido.getEstado());
        }

        Reclamo reclamo = new Reclamo();
        reclamo.setMotivo(dto.getMotivo());
        reclamo.setTipo(dto.getTipo());
        reclamo.setEstado(EstadoReclamo.ABIERTO);
        reclamo.setFechaReclamo(LocalDate.now());
        reclamo.setUsuario(usuario);
        reclamo.setPedido(pedido);

        return toResponseDto(reclamoRepository.save(reclamo));
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ReclamoResponseDto obtenerPorId(int id) {
        return toResponseDto(obtenerReclamoOException(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public List<ReclamoResponseDto> listarTodos() {
        return reclamoRepository.findAll().stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")    
    public List<ReclamoResponseDto> listarPorCliente(int usuarioID) {
        if (!usuarioRepository.existsById(usuarioID)) {
            throw new ResourceNotFoundException("Usuario", usuarioID);
        }
        return reclamoRepository.findByUsuarioUsuarioID(usuarioID).stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReclamoResponseDto> listarPorEstado(String estado) {
        EstadoReclamo estadoEnum = EstadoReclamo.valueOf(estado.toUpperCase());
        return reclamoRepository.findByEstado(estadoEnum).stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ReclamoResponseDto cambiarEstado(int id, String nuevoEstado) {
        Reclamo reclamo = obtenerReclamoOException(id);
        reclamo.setEstado(EstadoReclamo.valueOf(nuevoEstado.toUpperCase()));
        return toResponseDto(reclamoRepository.save(reclamo));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ReclamoResponseDto update(int id, ReclamoUpdateDto dto) {
        Reclamo reclamo = obtenerReclamoOException(id);

        // No se puede modificar un reclamo ya cerrado
        if (reclamo.getEstado() == EstadoReclamo.CERRADO) {
            throw new BadRequestException("No se puede modificar un reclamo en estado CERRADO");
        }

        if (dto.getMotivo() != null && !dto.getMotivo().isBlank()) {
            reclamo.setMotivo(dto.getMotivo());
        }
        if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
            reclamo.setTipo(dto.getTipo());
        }
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            EstadoReclamo nuevoEstado;
            try {
                nuevoEstado = EstadoReclamo.valueOf(dto.getEstado().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Estado de reclamo inválido: " + dto.getEstado()
                    + ". Valores válidos: ABIERTO, EN_PROCESO, RESUELTO, CERRADO");
            }
            reclamo.setEstado(nuevoEstado);
        }

        return toResponseDto(reclamoRepository.save(reclamo));
    }

    //Revisar

    private Reclamo obtenerReclamoOException(int id) {
        return reclamoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reclamo", id));
    }

    private ReclamoResponseDto toResponseDto(Reclamo r) {
        ReclamoResponseDto dto = new ReclamoResponseDto();
        dto.setReclamoID(r.getReclamoID());
        dto.setMotivo(r.getMotivo());
        dto.setTipo(r.getTipo());
        dto.setEstado(r.getEstado());
        dto.setFechaReclamo(r.getFechaReclamo());
        if (r.getPedido() != null) dto.setPedidoID(r.getPedido().getPedidoID());
        if (r.getUsuario() != null) {
            dto.setUsuarioID(r.getUsuario().getUsuarioID());
            dto.setUsuarioNombre(r.getUsuario().getNombre() + " " + r.getUsuario().getApellido());
        }
        return dto;
    }
}
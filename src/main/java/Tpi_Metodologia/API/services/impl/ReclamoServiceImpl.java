package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.dtos.registrar.ReclamoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ReclamoResponseDto;
import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.models.Reclamo;
import Tpi_Metodologia.API.models.Usuario;
import Tpi_Metodologia.API.repositories.PedidoRepository;
import Tpi_Metodologia.API.repositories.ReclamoRepository;
import Tpi_Metodologia.API.services.interfaces.IReclamoService;
import Tpi_Metodologia.API.utility.EstadoReclamo;
import Tpi_Metodologia.API.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ReclamoResponseDto crear(ReclamoRegistroDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioID())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioID()));

        Pedido pedido = pedidoRepository.findById(dto.getPedidoID())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", dto.getPedidoID()));

        // Validar que el pedido pertenece al cliente
        if (pedido.getUsuario().getUsuarioID() != dto.getUsuarioID()) {
            throw new BadRequestException("El pedido no pertenece al cliente indicado");
        }

        // Validar que el pedido está en un estado reclamable
        if ("CANCELADO".equals(pedido.getEstado()) || "PENDIENTE".equals(pedido.getEstado())) {
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
    public ReclamoResponseDto obtenerPorId(int id) {
        return toResponseDto(obtenerReclamoOException(id));
    }

    @Override
    public List<ReclamoResponseDto> listarTodos() {
        return reclamoRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReclamoResponseDto> listarPorCliente(int usuarioID) {
        if (!usuarioRepository.existsById(usuarioID)) {
            throw new ResourceNotFoundException("usuario", usuarioID);
        }
        return reclamoRepository.findByUsuarioUsuarioID(usuarioID).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReclamoResponseDto> listarPorEstado(String estado) {
        return reclamoRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReclamoResponseDto cambiarEstado(int id, String nuevoEstado) {
        Reclamo reclamo = obtenerReclamoOException(id);
        reclamo.setEstado(EstadoReclamo.valueOf(nuevoEstado.toUpperCase()));
        return toResponseDto(reclamoRepository.save(reclamo));
    }

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
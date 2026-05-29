package Tpi_Metodologia.API.mappers;

import java.time.LocalDate;

import Tpi_Metodologia.API.dtos.registrar.ReclamoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ReclamoResponseDto;
import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.models.Reclamo;
import Tpi_Metodologia.API.models.Usuario;
import Tpi_Metodologia.API.repositories.PedidoRepository;
import Tpi_Metodologia.API.repositories.UsuarioRepository;
import Tpi_Metodologia.API.utility.EstadoReclamo;

public class ReclamoMapper {

    public static Reclamo toEntity(ReclamoRegistroDto dto,PedidoRepository pedidoRepository,UsuarioRepository usuarioRepository) {
 
        Reclamo reclamo = new Reclamo();
 
        reclamo.setMotivo(dto.getMotivo());
        reclamo.setTipo(dto.getTipo());
        reclamo.setEstado(EstadoReclamo.ABIERTO); // estado inicial por defecto
        reclamo.setFechaReclamo(LocalDate.now());
 
        Pedido pedido = pedidoRepository.findById(dto.getPedidoID())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + dto.getPedidoID()));
        reclamo.setPedido(pedido);
 
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioID())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioID()));
        reclamo.setUsuario(usuario);
 
        return reclamo;
    }

    public static ReclamoResponseDto toDto(Reclamo reclamo){

        ReclamoResponseDto dto = new ReclamoResponseDto();
        
        dto.setReclamoID(reclamo.getReclamoID());
        dto.setMotivo(reclamo.getMotivo());
        dto.setTipo(reclamo.getTipo());
        dto.setEstado(reclamo.getEstado());
        dto.setPedidoID(reclamo.getPedido().getPedidoID());
        dto.setUsuarioID(reclamo.getUsuario().getUsuarioID());
        dto.setUsuarioNombre(reclamo.getUsuario().getNombre());
        dto.setFechaReclamo(reclamo.getFechaReclamo());

        return dto;
    }
    
}

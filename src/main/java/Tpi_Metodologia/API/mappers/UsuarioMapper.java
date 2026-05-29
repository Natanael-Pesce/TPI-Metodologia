package Tpi_Metodologia.API.mappers;

import Tpi_Metodologia.API.dtos.response.UsuarioResponseDto;
import Tpi_Metodologia.API.models.Usuario;

public class UsuarioMapper {
    
    public static Usuario toEntity(UsuarioResponseDto dto){
        Usuario usuario = new Usuario();

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());

        return usuario;
    }

    public static UsuarioResponseDto toDto(Usuario usuario){

        UsuarioResponseDto dto = new UsuarioResponseDto();

        dto.setUsuarioID(usuario.getUsuarioID());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());

        return dto;
    }
}

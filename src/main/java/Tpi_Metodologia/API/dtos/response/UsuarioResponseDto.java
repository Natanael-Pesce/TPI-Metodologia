package Tpi_Metodologia.API.dtos.response;

import Tpi_Metodologia.API.utility.Rol;
import lombok.Data;

@Data
public class UsuarioResponseDto {
    
    private int usuarioID;
    private String nombre;
    private String apellido;
    private String correo;
    private Rol rol;
}

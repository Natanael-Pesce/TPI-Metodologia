package Tpi_Metodologia.API.dtos.update;

import Tpi_Metodologia.API.utility.Rol;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UsuarioUpdateDto {

    private String nombre;
    private String apellido;

    @Email(message = "Formato de correo inválido")
    private String correo;

    private String contrasena;

    private Rol rol;
    
}

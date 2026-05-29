package Tpi_Metodologia.API.dtos.registrar;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRegistroDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    // Domicilio se puede crear inline o agregar después via /clientes/{id}/domicilios
    private DomicilioRegistroDto domicilio;

    // Cupón opcional al registrarse
    private String codigoCupon;

    //@NotBlank(message = "Debe de ingresar un cuil")
    //opcinal
    private String cuit;
}

package Tpi_Metodologia.API.dtos.auth;

import Tpi_Metodologia.API.utility.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private int usuarioID;
    private String nombre;
    private String apellido;
    private String correo;
    private Rol rol;
}
package Tpi_Metodologia.API.security;

import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.dtos.auth.AuthResponse;
import Tpi_Metodologia.API.dtos.auth.LoginRequest;
import Tpi_Metodologia.API.dtos.auth.RegisterRequest;
import Tpi_Metodologia.API.models.Usuario;
import Tpi_Metodologia.API.repositories.UsuarioRepository;
import Tpi_Metodologia.API.utility.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new BadRequestException("El correo " + request.getCorreo() + " ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(Rol.ROLE_CLIENTE)   // Por defecto, todo registro público es cliente
                .cuit(request.getCuit())
                .domicilios(new ArrayList<>())
                .build();

        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .usuarioID(usuario.getUsuarioID())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Delega la verificación de correo+contraseña al AuthenticationManager
        // Si falla lanza BadCredentialsException → 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getContrasena()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BadRequestException("Credenciales incorrectas"));

        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .usuarioID(usuario.getUsuarioID())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol())
                .build();
    }
}


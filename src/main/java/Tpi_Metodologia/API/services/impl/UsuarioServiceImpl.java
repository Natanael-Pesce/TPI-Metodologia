package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.config.exceptions.BadRequestException;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.dtos.registrar.DomicilioRegistroDto;
import Tpi_Metodologia.API.dtos.registrar.UsuarioRegistroDto;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.response.UsuarioResponseDto;
import Tpi_Metodologia.API.dtos.update.UsuarioUpdateDto;
import Tpi_Metodologia.API.models.Cupon;
import Tpi_Metodologia.API.models.Domicilio;
import Tpi_Metodologia.API.models.Usuario;
import Tpi_Metodologia.API.repositories.CuponRepository;
import Tpi_Metodologia.API.repositories.DomicilioRepository;
import Tpi_Metodologia.API.repositories.UsuarioRepository;
import Tpi_Metodologia.API.services.interfaces.IUsuarioService;
import Tpi_Metodologia.API.utility.Rol;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DomicilioRepository domicilioRepository;
    private final CuponRepository cuponRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDto registrar(UsuarioRegistroDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new BadRequestException("El correo " + dto.getCorreo() + " ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setCuit(dto.getCuit());
        usuario.setDomicilios(new ArrayList<>());

        if (dto.getDomicilio() != null) {
            Domicilio domicilio = toDomicilioEntity(dto.getDomicilio());
            domicilio = domicilioRepository.save(domicilio);
            usuario.getDomicilios().add(domicilio);
        }

        if (dto.getCodigoCupon() != null && !dto.getCodigoCupon().isBlank()) {
            Cupon cupon = cuponRepository.findByCodigo(dto.getCodigoCupon())
                    .orElseThrow(() -> new BadRequestException("Cupón con código '" + dto.getCodigoCupon() + "' no encontrado"));
            validarCupon(cupon);
            usuario.setCupon(cupon);
        }

        return toResponseDto(usuarioRepository.save(usuario));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.usuarioID")
    public UsuarioResponseDto obtenerporId(int id) {
        return toResponseDto(obtenerOException(id));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponseDto> listarTodos() {
        return usuarioRepository.findAll()
            .stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
        @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.usuarioID")
    public UsuarioResponseDto actualizar(int id, UsuarioUpdateDto dto) {
        Usuario usuario = obtenerOException(id);

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getApellido() != null) usuario.setApellido(dto.getApellido());
        if (dto.getCorreo() != null) {
            usuarioRepository.findByCorreo(dto.getCorreo()).ifPresent(u -> {
                if (u.getUsuarioID() != id) {
                    throw new BadRequestException("El correo " + dto.getCorreo() + " ya está en uso");
                }
            });
            usuario.setCorreo(dto.getCorreo());
        }
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        if (dto.getRol() != null){
            if (usuario.getRol() != Rol.ROLE_ADMINISTRADOR){
                throw new AccessDeniedException("Solo un administrador puede cambiar el rol");
            }
            usuario.setRol(dto.getRol());
        }

        return toResponseDto(usuarioRepository.save(usuario));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(int id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("SuperUsuario", id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #usuarioID == authentication.principal.usuarioID")
    public DomicilioResponseDto agregarDomicilio(int usuarioID, DomicilioRegistroDto dto) {
        Usuario usuario = obtenerOException(usuarioID);
        Domicilio domicilio = toDomicilioEntity(dto);
        domicilio = domicilioRepository.save(domicilio);
        usuario.getDomicilios().add(domicilio);
        usuarioRepository.save(usuario);
        return toDomicilioResponseDto(domicilio);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or #usuarioID == authentication.principal.usuarioID")
    public List<DomicilioResponseDto> listarDomicilios(int usuarioID) {
        Usuario usuario = obtenerOException(usuarioID);
        return usuario.getDomicilios()
            .stream()
            .map(this::toDomicilioResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #usuarioID == authentication.principal.usuarioID")
    public void eliminarDomicilio(int usuarioID, int domicilioID) {
        Usuario usuario = obtenerOException(usuarioID);
        boolean removed = usuario.getDomicilios().removeIf(d -> d.getDomicilioID() == domicilioID);
        if (!removed) {
            throw new ResourceNotFoundException("Domicilio con ID " + domicilioID + " no encontrado para este usuario");
        }
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #usuarioID == authentication.principal.usuarioID")
    public UsuarioResponseDto aplicarCupon(int usuarioID, String codigoCupon) {
        Usuario usuario = obtenerOException(usuarioID);
        Cupon cupon = cuponRepository.findByCodigo(codigoCupon)
            .orElseThrow(() -> new BadRequestException("Cupón '" + codigoCupon + "' no encontrado"));
        validarCupon(cupon);
        usuario.setCupon(cupon);
        return toResponseDto(usuarioRepository.save(usuario));
    }

    @Override
    @Deprecated
    public UsuarioResponseDto login(String correo, String contrasena) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new BadRequestException("Correo o contraseña incorrectos"));
        // En producción: BCrypt.matches(contrasena, superUsuario.getContrasena())
        if (!usuario.getContrasena().equals(contrasena)) {
            throw new BadRequestException("Correo o contraseña incorrectos");
        }
        return toResponseDto(usuario);
    }

    //Revisar
    private Usuario obtenerOException(int id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("SuperUsuario", id));
    }

    private void validarCupon(Cupon cupon) {
        if (!cupon.isEstado()) {
            throw new BadRequestException("El cupón no está activo");
        }
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(cupon.getFechaInicio()) || hoy.isAfter(cupon.getFechaFin())) {
            throw new BadRequestException("El cupón está vencido o aún no está vigente");
        }
    }

    private Domicilio toDomicilioEntity(DomicilioRegistroDto dto) {
        Domicilio d = new Domicilio();
        d.setPais(dto.getPais());
        d.setProvincia(dto.getProvincia());
        d.setCiudad(dto.getCiudad());
        d.setCalle(dto.getCalle());
        d.setNro(dto.getNro());
        d.setDepartamento(dto.getDepartamento());
        d.setNroDepartamento(dto.getNroDepartamento());
        d.setPiso(dto.getPiso());
        return d;
    }

    private DomicilioResponseDto toDomicilioResponseDto(Domicilio d) {
        DomicilioResponseDto dto = new DomicilioResponseDto();
        dto.setDomicilioID(d.getDomicilioID());
        dto.setPais(d.getPais());
        dto.setProvincia(d.getProvincia());
        dto.setCiudad(d.getCiudad());
        dto.setCalle(d.getCalle());
        dto.setNro(d.getNro());
        dto.setDepartamento(d.getDepartamento());
        dto.setNroDepartamento(d.getNroDepartamento());
        dto.setPiso(d.getPiso());
        return dto;
    }

    private UsuarioResponseDto toResponseDto(Usuario u) {
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setUsuarioID(u.getUsuarioID());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setRol(u.getRol());
        return dto;
    }
}
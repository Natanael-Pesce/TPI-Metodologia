package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.login.UsuarioLoginDto;
import Tpi_Metodologia.API.dtos.registrar.DomicilioRegistroDto;
import Tpi_Metodologia.API.dtos.registrar.UsuarioRegistroDto;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.response.UsuarioResponseDto;
import Tpi_Metodologia.API.dtos.update.UsuarioUpdateDto;
import Tpi_Metodologia.API.services.interfaces.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService UsuarioService;

    // POST /api/usuarios → registrar
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> registrar(@Valid @RequestBody UsuarioRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioService.registrar(dto));
    }

    // GET /api/usuarios
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos() {
        return ResponseEntity.ok(UsuarioService.listarTodos());
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.usuarioID")
    public ResponseEntity<UsuarioResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(UsuarioService.obtenerporId(id));
    }

    // PATCH /api/usuarios/{id}
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.usuarioID")
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable int id,
            @Valid @RequestBody UsuarioUpdateDto dto) {
        return ResponseEntity.ok(UsuarioService.actualizar(id, dto));
    }

    // DELETE /api/usuarios/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        UsuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Domicilios ---

    // GET /api/superusuarios/{id}/domicilios
    @GetMapping("/{id}/domicilios")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #usuarioId == authentication.principal.usuarioID")
    public ResponseEntity<List<DomicilioResponseDto>> listarDomicilios(@PathVariable int id) {
        return ResponseEntity.ok(UsuarioService.listarDomicilios(id));
    }

    // POST /api/usuarios/{id}/domicilios
    @PostMapping("/{id}/domicilios")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.usuarioID")
    public ResponseEntity<DomicilioResponseDto> agregarDomicilio(
            @PathVariable int id,
            @Valid @RequestBody DomicilioRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioService.agregarDomicilio(id, dto));
    }

    // DELETE /api/usuarios/{usuarioId}/domicilios/{domicilioId}
    @DeleteMapping("/{usuarioId}/domicilios/{domicilioId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #usuarioId == authentication.principal.usuarioID")
    public ResponseEntity<Void> eliminarDomicilio(
            @PathVariable int usuarioID,
            @PathVariable int domicilioId) {
        UsuarioService.eliminarDomicilio(usuarioID, domicilioId);
        return ResponseEntity.noContent().build();
    }

    // POST /api/usuarios/{id}/cupones → aplicar cupón
    @PostMapping("/{id}/cupones")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #usuarioId == authentication.principal.usuarioID")
    public ResponseEntity<UsuarioResponseDto> aplicarCupon(
            @PathVariable int id,
            @RequestParam String codigo) {
        return ResponseEntity.ok(UsuarioService.aplicarCupon(id, codigo));
    }

    // POST /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDto> login(@Valid @RequestBody UsuarioLoginDto dto) {
        return ResponseEntity.ok(UsuarioService.login(dto.getCorreo(), dto.getContrasena()));
    }
}

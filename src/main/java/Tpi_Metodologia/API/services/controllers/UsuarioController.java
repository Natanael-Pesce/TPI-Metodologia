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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService UsuarioService;

    // POST /api/superusuarios → registrar
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> registrar(@Valid @RequestBody UsuarioRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioService.registrar(dto));
    }

    // GET /api/superusuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos() {
        return ResponseEntity.ok(UsuarioService.listarTodos());
    }

    // GET /api/superusuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(UsuarioService.obtenerporId(id));
    }

    // PATCH /api/superusuarios/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable int id,
            @Valid @RequestBody UsuarioUpdateDto dto) {
        return ResponseEntity.ok(UsuarioService.actualizar(id, dto));
    }

    // DELETE /api/superusuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        UsuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Domicilios ---

    // GET /api/superusuarios/{id}/domicilios
    @GetMapping("/{id}/domicilios")
    public ResponseEntity<List<DomicilioResponseDto>> listarDomicilios(@PathVariable int id) {
        return ResponseEntity.ok(UsuarioService.listarDomicilios(id));
    }

    // POST /api/superusuarios/{id}/domicilios
    @PostMapping("/{id}/domicilios")
    public ResponseEntity<DomicilioResponseDto> agregarDomicilio(
            @PathVariable int id,
            @Valid @RequestBody DomicilioRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioService.agregarDomicilio(id, dto));
    }

    // DELETE /api/superusuarios/{usuarioId}/domicilios/{domicilioId}
    @DeleteMapping("/{usuarioId}/domicilios/{domicilioId}")
    public ResponseEntity<Void> eliminarDomicilio(
            @PathVariable int usuarioId,
            @PathVariable int domicilioId) {
        UsuarioService.eliminarDomicilio(usuarioId, domicilioId);
        return ResponseEntity.noContent().build();
    }

    // POST /api/superusuarios/{id}/cupones → aplicar cupón
    @PostMapping("/{id}/cupones")
    public ResponseEntity<UsuarioResponseDto> aplicarCupon(
            @PathVariable int id,
            @RequestParam String codigo) {
        return ResponseEntity.ok(UsuarioService.aplicarCupon(id, codigo));
    }

    // POST /api/superusuarios/login
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDto> login(@Valid @RequestBody UsuarioLoginDto dto) {
        return ResponseEntity.ok(UsuarioService.login(dto.getCorreo(), dto.getContrasena()));
    }
}

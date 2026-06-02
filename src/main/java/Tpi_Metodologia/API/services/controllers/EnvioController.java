package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.response.EnvioResponseDto;
import Tpi_Metodologia.API.services.interfaces.IEnvioService;
import Tpi_Metodologia.API.utility.EstadoEnvio;
import Tpi_Metodologia.API.dtos.update.EnvioUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final IEnvioService envioService;

    // GET /api/envios
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ResponseEntity<List<EnvioResponseDto>> listarTodos(
            @RequestParam(required = false) EstadoEnvio estado) {
        if (estado != null) {
            return ResponseEntity.ok(envioService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(envioService.listarTodos());
    }

    // GET /api/envios/{id}
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnvioResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    // GET /api/envios/tracking/{codigo}
    @GetMapping("/tracking/{codigo}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnvioResponseDto> obtenerPorTracking(@PathVariable String codigo) {
        return ResponseEntity.ok(envioService.obtenerPorTracking(codigo));
    }

    // PATCH /api/envios/{id}/estado
    // Body: { "estado": "DESPACHADO", "tracking": "TRK-2024-ABC" }
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ResponseEntity<EnvioResponseDto> actualizarEstado(
            @PathVariable int id,
            @RequestParam EstadoEnvio estado,
            @RequestParam(required = false) String tracking) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, estado, tracking));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ResponseEntity<EnvioResponseDto> update(
            @PathVariable int id,
            @Valid @RequestBody EnvioUpdateDto dto) {
        return ResponseEntity.ok(envioService.update(id, dto));
    }
}
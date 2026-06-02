package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.response.PagoResponseDto;
import Tpi_Metodologia.API.services.interfaces.IPagoService;
import Tpi_Metodologia.API.utility.EstadoPago;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final IPagoService pagoService;

    // GET /api/pagos
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PagoResponseDto>> listarTodos(
            @RequestParam(required = false) EstadoPago estado) {
        if (estado != null) {
            return ResponseEntity.ok(pagoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    // GET /api/pagos/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagoResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    // POST /api/pagos/{id}/aprobar
    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagoResponseDto> aprobar(@PathVariable int id) {
        return ResponseEntity.ok(pagoService.aprobarPago(id));
    }

    // POST /api/pagos/{id}/rechazar
    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PagoResponseDto> rechazar(@PathVariable int id) {
        return ResponseEntity.ok(pagoService.rechazarPago(id));
    }
}


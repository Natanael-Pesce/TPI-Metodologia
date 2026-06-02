package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.update.PedidoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.PedidoRegistroDto;
import Tpi_Metodologia.API.dtos.response.PedidoResponseDto;
import Tpi_Metodologia.API.services.interfaces.IPedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final IPedidoService pedidoService;

    // POST /api/pedidos
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponseDto> crear(@Valid @RequestBody PedidoRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    // GET /api/pedidos
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ResponseEntity<List<PedidoResponseDto>> listarTodos(
            @RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // GET /api/pedidos/{id}
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    // GET /api/pedidos/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidoResponseDto>> listarPorCliente(@PathVariable int clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    // PATCH /api/pedidos/{id}/estado
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ResponseEntity<PedidoResponseDto> actualizarEstado(
            @PathVariable int id,
            @RequestBody PedidoUpdateDto dto) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, dto));
    }

    // DELETE /api/pedidos/{id}/cancelar
    @DeleteMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelar(@PathVariable int id) {
        pedidoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
    // POST /api/pedidos/{id}/confirmar
    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ResponseEntity<PedidoResponseDto> confirmar(@PathVariable int id) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(id));
    }

}
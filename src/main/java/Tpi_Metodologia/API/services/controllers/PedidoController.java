package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.update.PedidoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.PedidoRegistroDto;
import Tpi_Metodologia.API.dtos.response.PedidoResponseDto;
import Tpi_Metodologia.API.services.interfaces.IPedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final IPedidoService pedidoService;

    // POST /api/pedidos
    @PostMapping
    public ResponseEntity<PedidoResponseDto> crear(@Valid @RequestBody PedidoRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    // GET /api/pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponseDto>> listarTodos(
            @RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // GET /api/pedidos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    // GET /api/pedidos/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDto>> listarPorCliente(@PathVariable int clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    // PATCH /api/pedidos/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDto> actualizarEstado(
            @PathVariable int id,
            @RequestBody PedidoUpdateDto dto) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, dto));
    }

    // DELETE /api/pedidos/{id}/cancelar
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable int id) {
        pedidoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
    // POST /api/pedidos/{id}/confirmar
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<PedidoResponseDto> confirmar(@PathVariable int id) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(id));
    }

}
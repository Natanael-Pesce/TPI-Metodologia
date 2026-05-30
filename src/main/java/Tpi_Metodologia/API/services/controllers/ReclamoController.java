package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.registrar.ReclamoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ReclamoResponseDto;
import Tpi_Metodologia.API.dtos.update.ReclamoUpdateDto;
import Tpi_Metodologia.API.services.interfaces.IReclamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamos")
@RequiredArgsConstructor
public class ReclamoController {

    private final IReclamoService reclamoService;

    @PostMapping
    public ResponseEntity<ReclamoResponseDto> crear(@Valid @RequestBody ReclamoRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reclamoService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReclamoResponseDto>> listarTodos(
            @RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            return ResponseEntity.ok(reclamoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(reclamoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReclamoResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(reclamoService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReclamoResponseDto>> listarPorCliente(@PathVariable int clienteId) {
        return ResponseEntity.ok(reclamoService.listarPorCliente(clienteId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReclamoResponseDto> cambiarEstado(
            @PathVariable int id,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(reclamoService.cambiarEstado(id, nuevoEstado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReclamoResponseDto> update(
            @PathVariable int id,
            @RequestBody ReclamoUpdateDto dto) {
        return ResponseEntity.ok(reclamoService.update(id, dto));
    }
}
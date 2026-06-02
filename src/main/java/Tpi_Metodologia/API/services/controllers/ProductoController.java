package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.update.ProductoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.ProductoRegistroDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;
import Tpi_Metodologia.API.services.interfaces.IProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;

    // POST /api/productos
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody ProductoRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(dto));
    }

    // GET /api/productos
    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> listarTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false, defaultValue = "false") boolean soloActivos) {

        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
        }
        if (soloActivos) {
            return ResponseEntity.ok(productoService.listarActivos());
        }
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // GET /api/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // GET /api/productos/stock-bajo
    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR')")
    public ResponseEntity<List<ProductoResponseDto>> listarConStockBajo() {
        return ResponseEntity.ok(productoService.listarConStockBajo());
    }

    // PATCH /api/productos/{id}
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductoResponseDto> actualizar(
            @PathVariable int id,
            @Valid @RequestBody ProductoUpdateDto dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }

    // PATCH /api/productos/{id}/estado?activo=true
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductoResponseDto> cambiarEstado(
            @PathVariable int id,
            @RequestParam boolean activo) {
        return ResponseEntity.ok(productoService.cambiarEstado(id, activo));
    }

    // DELETE /api/productos/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
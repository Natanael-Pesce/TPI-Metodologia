package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.registrar.KitRegistroDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;
import Tpi_Metodologia.API.services.interfaces.IKitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kits")
@RequiredArgsConstructor
public class KitController {

    private final IKitService kitService;

    // POST /api/kits
    @PostMapping
    public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody KitRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kitService.crear(dto));
    }

    // GET /api/kits
    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> listarTodos() {
        return ResponseEntity.ok(kitService.listarTodos());
    }

    // GET /api/kits/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(kitService.obtenerPorId(id));
    }

    // PUT /api/kits/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizar(
            @PathVariable int id,
            @Valid @RequestBody KitRegistroDto dto) {
        return ResponseEntity.ok(kitService.actualizar(id, dto));
    }

    // DELETE /api/kits/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        kitService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
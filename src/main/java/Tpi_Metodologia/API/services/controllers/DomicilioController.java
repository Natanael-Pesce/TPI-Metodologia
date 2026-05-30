package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.registrar.DomicilioRegistroDto;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.update.DomicilioUpdateDto;
import Tpi_Metodologia.API.services.interfaces.IDomicilioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domicilios")
@RequiredArgsConstructor
public class DomicilioController {

    private final IDomicilioService domicilioService;

    // POST /api/domicilios
    @PostMapping
    public ResponseEntity<DomicilioResponseDto> crear(
            @Valid @RequestBody DomicilioRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(domicilioService.crear(dto));
    }

    // GET /api/domicilios
    @GetMapping
    public ResponseEntity<List<DomicilioResponseDto>> listarTodos() {
        return ResponseEntity.ok(domicilioService.listarTodos());
    }

    // GET /api/domicilios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DomicilioResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(domicilioService.obtenerPorId(id));
    }

    // PATCH /api/domicilios/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<DomicilioResponseDto> update(
            @PathVariable int id,
            @Valid @RequestBody DomicilioUpdateDto dto) {
        return ResponseEntity.ok(domicilioService.update(id, dto));
    }

    // DELETE /api/domicilios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        domicilioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
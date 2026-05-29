package Tpi_Metodologia.API.services.controllers;

import Tpi_Metodologia.API.dtos.registrar.CuponRegistroDto;
import Tpi_Metodologia.API.dtos.response.CuponResponseDto;
import Tpi_Metodologia.API.services.interfaces.ICuponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cupones")
@RequiredArgsConstructor
public class CuponController {

    private final ICuponService cuponService;

    // POST /api/cupones
    @PostMapping
    public ResponseEntity<CuponResponseDto> crear(@Valid @RequestBody CuponRegistroDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponService.crear(dto));
    }

    // GET /api/cupones
    @GetMapping
    public ResponseEntity<List<CuponResponseDto>> listarTodos(
            @RequestParam(required = false, defaultValue = "false") boolean soloActivos) {
        if (soloActivos) {
            return ResponseEntity.ok(cuponService.listarActivos());
        }
        return ResponseEntity.ok(cuponService.listarTodos());
    }

    // GET /api/cupones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CuponResponseDto> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(cuponService.obtenerPorId(id));
    }

    // GET /api/cupones/codigo/{codigo}
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CuponResponseDto> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(cuponService.obtenerPorCodigo(codigo));
    }

    // PUT /api/cupones/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CuponResponseDto> actualizar(
            @PathVariable int id,
            @Valid @RequestBody CuponRegistroDto dto) {
        return ResponseEntity.ok(cuponService.actualizar(id, dto));
    }

    // DELETE /api/cupones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        cuponService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
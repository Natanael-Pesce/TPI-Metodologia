package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.dtos.registrar.KitRegistroDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.models.Cupon;
import Tpi_Metodologia.API.models.Kit;
import Tpi_Metodologia.API.models.Producto;
import Tpi_Metodologia.API.repositories.CuponRepository;
import Tpi_Metodologia.API.repositories.KitRepository;
import Tpi_Metodologia.API.repositories.ProductoRepository;
import Tpi_Metodologia.API.services.interfaces.IKitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitServiceImpl implements IKitService {

    private final KitRepository kitRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;
    private final ProductoServiceImpl productoService; // reutilizamos el mapper

    @Override
    @Transactional
    public ProductoResponseDto crear(KitRegistroDto dto) {
        Kit kit = new Kit();
        kit.setNombreProducto(dto.getNombreProducto());
        kit.setPrecioProducto(dto.getPrecioProducto());
        kit.setImagen(dto.getImagen());
        kit.setStock(dto.getStock());
        kit.setStockMin(dto.getStockMin());
        kit.setProductoActivo(dto.isProductoActivo());

        if (dto.getCuponID() != null) {
            Cupon cupon = cuponRepository.findById(dto.getCuponID())
                    .orElseThrow(() -> new ResourceNotFoundException("Cupon", dto.getCuponID()));
            kit.setCupon(cupon);
        }

        // Cargar los productos que componen el kit
        List<Producto> productos = dto.getProductosIDs().stream()
                .map(pid -> productoRepository.findById(pid)
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", pid)))
                .collect(Collectors.toList());
        kit.setProductos(productos);

        return productoService.toResponseDto(kitRepository.save(kit));
    }

    @Override
    public ProductoResponseDto obtenerPorId(int id) {
        Kit kit = kitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kit", id));
        return productoService.toResponseDto(kit);
    }

    @Override
    public List<ProductoResponseDto> listarTodos() {
        return kitRepository.findAll()
                .stream()
                .map(productoService::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponseDto actualizar(int id, KitRegistroDto dto) {
        Kit kit = kitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kit", id));

        if (dto.getNombreProducto() != null) kit.setNombreProducto(dto.getNombreProducto());
        if (dto.getPrecioProducto() > 0) kit.setPrecioProducto(dto.getPrecioProducto());
        if (dto.getImagen() != null) kit.setImagen(dto.getImagen());
        if (dto.getStock() >= 0) kit.setStock(dto.getStock());
        kit.setStockMin(dto.getStockMin());
        kit.setProductoActivo(dto.isProductoActivo());

        if (dto.getProductosIDs() != null && !dto.getProductosIDs().isEmpty()) {
            List<Producto> productos = dto.getProductosIDs().stream()
                    .map(pid -> productoRepository.findById(pid)
                            .orElseThrow(() -> new ResourceNotFoundException("Producto", pid)))
                    .collect(Collectors.toList());
            kit.setProductos(productos);
        }

        return productoService.toResponseDto(kitRepository.save(kit));
    }

    @Override
    public void eliminar(int id) {
        if (!kitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kit", id);
        }
        kitRepository.deleteById(id);
    }
}
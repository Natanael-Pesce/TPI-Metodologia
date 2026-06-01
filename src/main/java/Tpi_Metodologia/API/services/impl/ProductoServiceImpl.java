package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.dtos.update.ProductoUpdateDto;
import Tpi_Metodologia.API.dtos.registrar.ProductoRegistroDto;
import Tpi_Metodologia.API.dtos.response.CuponResponseDto;
import Tpi_Metodologia.API.dtos.response.ProductoResponseDto;
import Tpi_Metodologia.API.config.exceptions.ResourceNotFoundException;
import Tpi_Metodologia.API.models.Cupon;
import Tpi_Metodologia.API.models.Producto;
import Tpi_Metodologia.API.repositories.CuponRepository;
import Tpi_Metodologia.API.repositories.ProductoRepository;
import Tpi_Metodologia.API.services.interfaces.IProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;

    @Override
    @Transactional
    public ProductoResponseDto crear(ProductoRegistroDto dto) {
        Producto producto = new Producto();
        producto.setNombreProducto(dto.getNombreProducto());
        producto.setPrecioProducto(dto.getPrecioProducto());
        producto.setImagen(dto.getImagen());
        producto.setStock(dto.getStock());
        producto.setStockMin(dto.getStockMin());
        producto.setProductoActivo(dto.isProductoActivo());

        if (dto.getCuponID() != null) {
            Cupon cupon = cuponRepository.findById(dto.getCuponID())
                .orElseThrow(() -> new ResourceNotFoundException("Cupon", dto.getCuponID()));
            producto.setCupon(cupon);
        }

        return toResponseDto(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDto obtenerPorId(int id) {
        return toResponseDto(obtenerProductoOException(id));
    }

    @Override
    public List<ProductoResponseDto> listarTodos() {
        return productoRepository.findAll()
            .stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDto> listarActivos() {
        return productoRepository.findByProductoActivoTrue()
            .stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreProductoContainingIgnoreCase(nombre)
            .stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDto> listarConStockBajo() {
        return productoRepository.findAll()
            .stream()
            .filter(p -> p.isProductoActivo() && p.getStock() <= p.getStockMin())
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponseDto actualizar(int id, ProductoUpdateDto dto) {
        Producto producto = obtenerProductoOException(id);

        if (dto.getNombreProducto() != null) producto.setNombreProducto(dto.getNombreProducto());
        if (dto.getPrecioProducto() != null) producto.setPrecioProducto(dto.getPrecioProducto());
        if (dto.getImagen() != null) producto.setImagen(dto.getImagen());
        if (dto.getStock() != null) producto.setStock(dto.getStock());
        if (dto.getStockMin() != null) producto.setStockMin(dto.getStockMin());
        if (dto.getProductoActivo() != null) producto.setProductoActivo(dto.getProductoActivo());
        if (dto.getCuponID() != null) {
            Cupon cupon = cuponRepository.findById(dto.getCuponID())
                .orElseThrow(() -> new ResourceNotFoundException("Cupon", dto.getCuponID()));
            producto.setCupon(cupon);
        }

        return toResponseDto(productoRepository.save(producto));
    }

    @Override
    public void eliminar(int id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductoResponseDto cambiarEstado(int id, boolean activo) {
        Producto producto = obtenerProductoOException(id);
        producto.setProductoActivo(activo);
        return toResponseDto(productoRepository.save(producto));
    }

    //Revisar

    private Producto obtenerProductoOException(int id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    public ProductoResponseDto toResponseDto(Producto p) {
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setProductoID(p.getProductoID());
        dto.setNombreProducto(p.getNombreProducto());
        dto.setPrecioProducto(p.getPrecioProducto());
        dto.setImagen(p.getImagen());
        dto.setStock(p.getStock());
        dto.setStockMin(p.getStockMin());
        dto.setProductoActivo(p.isProductoActivo());
        if (p.getCupon() != null) {
            dto.setCupon(toCuponResponseDto(p.getCupon()));
        }
        return dto;
    }

    private CuponResponseDto toCuponResponseDto(Cupon c) {
        CuponResponseDto dto = new CuponResponseDto();
        dto.setCuponID(c.getCuponID());
        dto.setCodigo(c.getCodigo());
        dto.setDescuento(c.getDescuento());
        dto.setEstado(c.isEstado());
        dto.setFechaInicio(c.getFechaInicio());
        dto.setFechaFin(c.getFechaFin());
        return dto;
    }
}
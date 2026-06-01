package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByProductoActivoTrue();

    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre);

    List<Producto> findByStockLessThanEqualAndProductoActivoTrue(int stockMin);
}
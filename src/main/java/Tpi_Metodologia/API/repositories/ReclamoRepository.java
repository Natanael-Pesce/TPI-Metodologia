package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {

    List<Reclamo> findByUsuarioUsuarioID(int usuarioID);

    List<Reclamo> findByEstado(String estado);

    List<Reclamo> findByPedidoPedidoID(int pedidoID);
}
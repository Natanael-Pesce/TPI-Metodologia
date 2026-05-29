package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Todos los pedidos de un cliente
    List<Pedido> findByUsuarioUsuarioID(int usuarioID);

    // Pedidos por estado (PENDIENTE, CONFIRMADO, etc.)
    List<Pedido> findByEstado(String estado);

    // Pedidos de un cliente por estado
    List<Pedido> findByUsuarioUsuarioIDAndEstado(int usuarioID, String estado);
}
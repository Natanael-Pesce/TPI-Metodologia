package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Pedido;
import Tpi_Metodologia.API.utility.EstadoPedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuarioUsuarioID(int usuarioID);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByUsuarioUsuarioIDAndEstado(int usuarioID, EstadoPedido estado);
}
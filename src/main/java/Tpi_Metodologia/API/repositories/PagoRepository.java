package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Pago;
import Tpi_Metodologia.API.utility.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByEstadoPago(EstadoPago estadoPago);
}


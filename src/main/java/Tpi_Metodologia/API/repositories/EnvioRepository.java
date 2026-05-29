package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Envio;
import Tpi_Metodologia.API.utility.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {

    List<Envio> findByEstadoEnvio(EstadoEnvio estadoEnvio);

    Optional<Envio> findByTracking(String tracking);
}

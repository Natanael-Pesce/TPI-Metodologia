package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Domicilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioRepository extends JpaRepository<Domicilio, Integer> {
}
package Tpi_Metodologia.API.repositories;

import Tpi_Metodologia.API.models.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitRepository extends JpaRepository<Kit, Integer> {
}
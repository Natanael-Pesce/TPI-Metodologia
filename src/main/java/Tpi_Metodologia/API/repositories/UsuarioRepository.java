package Tpi_Metodologia.API.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tpi_Metodologia.API.models.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findById(int usuarioID);

    boolean existsByCorreo(String correo);
}

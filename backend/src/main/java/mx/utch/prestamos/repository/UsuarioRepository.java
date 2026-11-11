package mx.utch.prestamos.repository;

import java.util.Optional;
import mx.utch.prestamos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}

package mx.utch.prestamos.repository;

import java.util.Collection;
import java.util.List;
import mx.utch.prestamos.model.EstadoPrestamo;
import mx.utch.prestamos.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findAllByOrderByIdDesc();

    List<Prestamo> findBySolicitanteUsernameOrderByIdDesc(String username);

    /** ¿El equipo ya tiene un préstamo en alguno de estos estados? */
    boolean existsByEquipoIdAndEstadoIn(Long equipoId, Collection<EstadoPrestamo> estados);
}

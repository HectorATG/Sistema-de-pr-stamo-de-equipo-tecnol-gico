package mx.utch.prestamos.repository;

import java.util.List;
import mx.utch.prestamos.model.Equipo;
import mx.utch.prestamos.model.EstadoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByEstado(EstadoEquipo estado);
}

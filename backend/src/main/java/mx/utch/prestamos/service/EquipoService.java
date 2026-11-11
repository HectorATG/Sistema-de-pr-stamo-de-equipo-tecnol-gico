package mx.utch.prestamos.service;

import java.util.List;
import mx.utch.prestamos.model.Equipo;
import mx.utch.prestamos.model.EstadoEquipo;
import mx.utch.prestamos.repository.EquipoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    /** Paso 1 del flujo: consultar disponibilidad. */
    @Transactional(readOnly = true)
    public List<Equipo> listarDisponibles() {
        return equipoRepository.findByEstado(EstadoEquipo.DISPONIBLE);
    }

    @Transactional(readOnly = true)
    public List<Equipo> listar(EstadoEquipo estado) {
        return estado == null ? equipoRepository.findAll() : equipoRepository.findByEstado(estado);
    }
}

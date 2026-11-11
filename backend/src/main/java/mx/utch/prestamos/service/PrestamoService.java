package mx.utch.prestamos.service;

import java.time.LocalDate;
import java.util.List;
import mx.utch.prestamos.dto.SolicitudRequest;
import mx.utch.prestamos.exception.NegocioException;
import mx.utch.prestamos.model.Equipo;
import mx.utch.prestamos.model.EstadoEquipo;
import mx.utch.prestamos.model.EstadoPrestamo;
import mx.utch.prestamos.model.Prestamo;
import mx.utch.prestamos.model.Usuario;
import mx.utch.prestamos.repository.EquipoRepository;
import mx.utch.prestamos.repository.PrestamoRepository;
import mx.utch.prestamos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio del préstamo (RN1 a RN4). Cada método es una sola
 * transacción: el cambio de estado del préstamo y del equipo ocurre junto
 * o no ocurre.
 */
@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           EquipoRepository equipoRepository,
                           UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Paso 2 del flujo. RN2: registra solicitante, equipo, fechas y estado SOLICITADO. */
    @Transactional
    public Prestamo solicitar(String username, SolicitudRequest dto) {
        Usuario solicitante = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> NegocioException.noEncontrado("Usuario no encontrado: " + username));
        Equipo equipo = equipoRepository.findById(dto.equipoId())
                .orElseThrow(() -> NegocioException.noEncontrado("No existe el equipo " + dto.equipoId()));

        if (dto.fechaFin().isBefore(dto.fechaInicio())) {
            throw NegocioException.solicitudInvalida("La fecha de fin no puede ser anterior a la de inicio");
        }
        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            throw NegocioException.conflicto("El equipo '" + equipo.getNombre() + "' no está disponible");
        }

        return prestamoRepository.save(new Prestamo(solicitante, equipo, dto.fechaInicio(), dto.fechaFin()));
    }

    /** Pasos 3 y 4 del flujo: validar disponibilidad y autorizar. */
    @Transactional
    public Prestamo autorizar(Long id) {
        Prestamo prestamo = buscarPorId(id);
        exigirEstado(prestamo, EstadoPrestamo.SOLICITADO, "autorizar");
        validarDisponibilidad(prestamo.getEquipo());
        prestamo.setEstado(EstadoPrestamo.AUTORIZADO);
        return prestamo;
    }

    @Transactional
    public Prestamo rechazar(Long id) {
        Prestamo prestamo = buscarPorId(id);
        exigirEstado(prestamo, EstadoPrestamo.SOLICITADO, "rechazar");
        prestamo.setEstado(EstadoPrestamo.RECHAZADO);
        return prestamo;
    }

    /** Paso 5 del flujo. RN3: al entregar, el equipo cambia a PRESTADO. */
    @Transactional
    public Prestamo entregar(Long id) {
        Prestamo prestamo = buscarPorId(id);
        exigirEstado(prestamo, EstadoPrestamo.AUTORIZADO, "entregar");
        prestamo.getEquipo().setEstado(EstadoEquipo.PRESTADO);
        prestamo.setFechaEntrega(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.ENTREGADO);
        return prestamo;
    }

    /** RN3: al devolver, el equipo vuelve a DISPONIBLE. */
    @Transactional
    public Prestamo devolver(Long id) {
        Prestamo prestamo = buscarPorId(id);
        exigirEstado(prestamo, EstadoPrestamo.ENTREGADO, "devolver");
        prestamo.getEquipo().setEstado(EstadoEquipo.DISPONIBLE);
        prestamo.setFechaDevolucion(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        return prestamo;
    }

    /** Paso 6 del flujo. RN4: los préstamos nunca se borran, así que esta lista es el historial. */
    @Transactional(readOnly = true)
    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPorSolicitante(String username) {
        return prestamoRepository.findBySolicitanteUsernameOrderByIdDesc(username);
    }

    @Transactional(readOnly = true)
    public Prestamo buscarPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> NegocioException.noEncontrado("No existe el préstamo " + id));
    }

    /**
     * RN1: no se autoriza un equipo no disponible. El equipo debe estar
     * DISPONIBLE y no tener otro préstamo ya autorizado o entregado.
     */
    private void validarDisponibilidad(Equipo equipo) {
        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            throw NegocioException.conflicto(
                    "No se puede autorizar: el equipo '" + equipo.getNombre() + "' no está disponible");
        }
        boolean comprometido = prestamoRepository.existsByEquipoIdAndEstadoIn(
                equipo.getId(), List.of(EstadoPrestamo.AUTORIZADO, EstadoPrestamo.ENTREGADO));
        if (comprometido) {
            throw NegocioException.conflicto(
                    "No se puede autorizar: el equipo '" + equipo.getNombre() + "' ya tiene un préstamo activo");
        }
    }

    private void exigirEstado(Prestamo prestamo, EstadoPrestamo esperado, String accion) {
        if (prestamo.getEstado() != esperado) {
            throw NegocioException.conflicto("No se puede " + accion + " un préstamo en estado "
                    + prestamo.getEstado() + " (debe estar " + esperado + ")");
        }
    }
}

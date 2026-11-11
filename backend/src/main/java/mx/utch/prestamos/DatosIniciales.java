package mx.utch.prestamos;

import mx.utch.prestamos.model.Equipo;
import mx.utch.prestamos.model.Rol;
import mx.utch.prestamos.model.TipoEquipo;
import mx.utch.prestamos.model.Usuario;
import mx.utch.prestamos.repository.EquipoRepository;
import mx.utch.prestamos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Carga usuarios y equipos al arrancar. Sustituye a las pantallas del
 * Administrador, que quedaron fuera del alcance del prototipo.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(UsuarioRepository usuarioRepository,
                          EquipoRepository equipoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.save(new Usuario("Ana López", "ana", passwordEncoder.encode("ana123"), Rol.SOLICITANTE));
        usuarioRepository.save(new Usuario("Luis Pérez", "luis", passwordEncoder.encode("luis123"), Rol.SOLICITANTE));
        usuarioRepository.save(new Usuario("Rosa Méndez", "rosa", passwordEncoder.encode("rosa123"), Rol.RESPONSABLE));
        usuarioRepository.save(new Usuario("Administrador", "admin", passwordEncoder.encode("admin123"), Rol.ADMIN));

        equipoRepository.save(new Equipo("Laptop Dell Latitude 5440", TipoEquipo.LAPTOP, "LAP-001"));
        equipoRepository.save(new Equipo("Laptop HP ProBook 450", TipoEquipo.LAPTOP, "LAP-002"));
        equipoRepository.save(new Equipo("Proyector Epson PowerLite", TipoEquipo.PROYECTOR, "PRY-001"));
        equipoRepository.save(new Equipo("Proyector BenQ MS560", TipoEquipo.PROYECTOR, "PRY-002"));
        equipoRepository.save(new Equipo("Kit Arduino Uno", TipoEquipo.KIT, "KIT-001"));
        equipoRepository.save(new Equipo("Kit Raspberry Pi 4", TipoEquipo.KIT, "KIT-002"));
    }
}

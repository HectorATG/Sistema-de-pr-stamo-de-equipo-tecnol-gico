package mx.utch.prestamos.dto;

import mx.utch.prestamos.model.Rol;
import mx.utch.prestamos.model.Usuario;

/** Usuario autenticado, sin la contraseña. */
public record UsuarioResponse(Long id, String nombre, String username, Rol rol) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getUsername(), u.getRol());
    }
}

package com.instituto.cafeteria.servicios;

import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioUsuarios {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ServicioNotificaciones servicioNotificaciones;

    public List<Usuario> obtenerTodos() {
        return repositorioUsuario.findAll();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repositorioUsuario.findById(email);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return repositorioUsuario.save(usuario);
    }

    public Usuario registrarUsuario(Usuario usuario, Usuario.Rol rol) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setEstado(true);
        Usuario guardado = repositorioUsuario.save(usuario);

        // Notificar a todos los ADMIN
        servicioNotificaciones.notificarARol("ADMIN",
                "NUEVO_USUARIO",
                "Nuevo usuario registrado",
                guardado.getNombre() + " se ha registrado con el rol " + rol + ".",
                "/admin/usuarios");

        return guardado;
    }

    public void cambiarRol(String email, Usuario.Rol nuevoRol) {
        repositorioUsuario.findById(email).ifPresent(usuario -> {
            usuario.setRol(nuevoRol);
            repositorioUsuario.save(usuario);
        });
    }

    public void cambiarEstado(String email, boolean estado) {
        repositorioUsuario.findById(email).ifPresent(usuario -> {
            usuario.setEstado(estado);
            repositorioUsuario.save(usuario);
        });
    }

    public long contarProfesores() {
        return repositorioUsuario.findAll().stream()
                .filter(u -> u.getRol() == Usuario.Rol.PROFESOR)
                .count();
    }
}

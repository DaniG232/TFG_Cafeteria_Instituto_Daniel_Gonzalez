package com.instituto.cafeteria.servicios;

import com.instituto.cafeteria.modelo.Notificacion;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.repositorios.RepositorioNotificacion;
import com.instituto.cafeteria.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioNotificaciones {

    @Autowired
    private RepositorioNotificacion repositorioNotificacion;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    public Notificacion crearNotificacion(String emailDestinatario, String tipo, String titulo, String mensaje, String urlAccion) {
        Notificacion notificacion = new Notificacion();
        notificacion.setEmailDestinatario(emailDestinatario);
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setUrlAccion(urlAccion);
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());
        return repositorioNotificacion.save(notificacion);
    }

    public void notificarATodosLosRoles(String tipo, String titulo, String mensaje, String urlAccion) {
        List<Usuario> usuariosActivos = repositorioUsuario.findByEstadoTrue();
        for (Usuario usuario : usuariosActivos) {
            crearNotificacion(usuario.getEmail(), tipo, titulo, mensaje, urlAccion);
        }
    }

    public void notificarARol(String rol, String tipo, String titulo, String mensaje, String urlAccion) {
        Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol);
        List<Usuario> usuarios = repositorioUsuario.findByRolAndEstadoTrue(rolEnum);
        for (Usuario usuario : usuarios) {
            crearNotificacion(usuario.getEmail(), tipo, titulo, mensaje, urlAccion);
        }
    }

    public List<Notificacion> obtenerNotificaciones(String email) {
        List<Notificacion> todas = repositorioNotificacion.findByEmailDestinatarioOrderByFechaCreacionDesc(email);
        return todas.stream().limit(20).toList();
    }

    public long contarNoLeidas(String email) {
        return repositorioNotificacion.countByEmailDestinatarioAndLeidaFalse(email);
    }

    public List<Notificacion> obtenerTodasLasNotificaciones(String email) {
        return repositorioNotificacion.findByEmailDestinatarioOrderByFechaCreacionDesc(email);
    }

    @Transactional
    public void marcarTodasComoLeidas(String email) {
        List<Notificacion> noLeidas = repositorioNotificacion.findByEmailDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(email);
        for (Notificacion n : noLeidas) {
            n.setLeida(true);
        }
        repositorioNotificacion.saveAll(noLeidas);
    }

    @Transactional
    public void marcarComoLeida(int idNotificacion, String email) {
        repositorioNotificacion.findById(idNotificacion).ifPresent(notificacion -> {
            if (notificacion.getEmailDestinatario().equals(email)) {
                notificacion.setLeida(true);
                repositorioNotificacion.save(notificacion);
            }
        });
    }

    @Transactional
    public void eliminarLeidas(String email) {
        repositorioNotificacion.deleteByEmailDestinatarioAndLeidaTrue(email);
    }

    /**
     * Calcula la fecha
     */
    public String calcularFechaRelativa(LocalDateTime fechaCreacion) {
        Duration duracion = Duration.between(fechaCreacion, LocalDateTime.now());
        long segundos = duracion.getSeconds();

        if (segundos < 60) {
            return "hace unos segundos";
        } else if (segundos < 3600) {
            long minutos = segundos / 60;
            return minutos == 1 ? "hace 1 minuto" : "hace " + minutos + " minutos";
        } else if (segundos < 86400) {
            long horas = segundos / 3600;
            return horas == 1 ? "hace 1 hora" : "hace " + horas + " horas";
        } else {
            long dias = segundos / 86400;
            return dias == 1 ? "hace 1 día" : "hace " + dias + " días";
        }
    }
}

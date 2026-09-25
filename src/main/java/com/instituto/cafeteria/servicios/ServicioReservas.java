package com.instituto.cafeteria.servicios;

import com.instituto.cafeteria.modelo.Menu;
import com.instituto.cafeteria.modelo.Notificacion;
import com.instituto.cafeteria.modelo.Reserva;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.repositorios.RepositorioReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicioReservas {

    @Autowired
    private RepositorioReserva repositorioReserva;

    @Autowired
    private ServicioNotificaciones servicioNotificaciones;

    public List<Reserva> obtenerTodas() {
        return repositorioReserva.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fechaReserva"));
    }

    public List<Reserva> obtenerPorUsuario(Usuario usuario) {
        return repositorioReserva.findByUsuarioOrderByFechaReservaDesc(usuario);
    }

    public Optional<Reserva> buscarPorId(Integer id) {
        return repositorioReserva.findById(id);
    }

    public Reserva crearReserva(Usuario usuario, Menu menu) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMenu(menu);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setPrecioPagado(menu.getPrecio());
        reserva.setEstado("PENDIENTE");
        reserva.setCodigoRecogida(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        reserva.setAsistido(false);
        Reserva reservaGuardada = repositorioReserva.save(reserva);

        // Notificar a todos los COCINERO y ADMIN
        String titulo = "Nueva reserva de " + usuario.getNombre();
        String mensaje = "El profesor " + usuario.getNombre() + " ha reservado el menú del "
                + menu.getFecha() + ". Código: " + reservaGuardada.getCodigoRecogida();
        servicioNotificaciones.notificarARol("COCINERO", Notificacion.NUEVA_RESERVA, titulo, mensaje, "/cocinero/reservas");
        servicioNotificaciones.notificarARol("ADMIN", Notificacion.NUEVA_RESERVA, titulo, mensaje, "/cocinero/reservas");

        return reservaGuardada;
    }

    public void cancelarReserva(Integer id, String emailUsuario) {
        repositorioReserva.findById(id).ifPresent(reserva -> {
            if (reserva.getUsuario().getEmail().equals(emailUsuario)) {
                reserva.setEstado("CANCELADO");
                repositorioReserva.save(reserva);
            }
        });
    }

    public void cambiarEstadoRecogida(Integer id, boolean recogido) {
        repositorioReserva.findById(id).ifPresent(reserva -> {
            reserva.setAsistido(recogido);
            reserva.setEstado(recogido ? "RECOGIDO" : "PENDIENTE");
            repositorioReserva.save(reserva);

            // Notificar al profesor propietario solo si se marca como recogida
            if (recogido) {
                String titulo = "Tu reserva ha sido marcada como recogida";
                String mensaje = "Tu menú del " + reserva.getMenu().getFecha()
                        + " con código " + reserva.getCodigoRecogida() + " ha sido entregado.";
                servicioNotificaciones.crearNotificacion(
                        reserva.getUsuario().getEmail(),
                        Notificacion.RESERVA_LISTA, titulo, mensaje, "/profesor/reservas");
            }
        });
    }

    public void cambiarEstado(Integer id, String estado) {
        repositorioReserva.findById(id).ifPresent(reserva -> {
            reserva.setEstado(estado);
            reserva.setAsistido("RECOGIDO".equals(estado));
            repositorioReserva.save(reserva);

            // Notificar al profesor propietario solo si se marca como recogida
            if ("RECOGIDO".equals(estado)) {
                String titulo = "Tu reserva ha sido marcada como recogida";
                String mensaje = "Tu menú del " + reserva.getMenu().getFecha()
                        + " con código " + reserva.getCodigoRecogida() + " ha sido entregado.";
                servicioNotificaciones.crearNotificacion(
                        reserva.getUsuario().getEmail(),
                        Notificacion.RESERVA_LISTA, titulo, mensaje, "/profesor/reservas");
            }
        });
    }

    public long contarReservasHoy() {
        return repositorioReserva.countByFechaReserva(LocalDate.now());
    }

    public List<Reserva> obtenerReservasHoy() {
        return repositorioReserva.findByFechaReserva(LocalDate.now());
    }
}

package com.instituto.cafeteria.controladores;

import com.instituto.cafeteria.modelo.Menu;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.modelo.Reserva;
import com.instituto.cafeteria.modelo.Notificacion;
import com.instituto.cafeteria.repositorios.RepositorioReserva;
import com.instituto.cafeteria.servicios.ServicioMenus;
import com.instituto.cafeteria.servicios.ServicioReservas;
import com.instituto.cafeteria.servicios.ServicioUsuarios;
import com.instituto.cafeteria.servicios.ServicioPago;
import com.instituto.cafeteria.servicios.ServicioPago.ResultadoPago;
import com.instituto.cafeteria.servicios.ServicioNotificaciones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class ControladorReserva {

    @Autowired private ServicioReservas servicioReservas;
    @Autowired private ServicioUsuarios servicioUsuarios;
    @Autowired private ServicioMenus servicioMenus;
    @Autowired private RepositorioReserva repositorioReserva;
    @Autowired private ServicioPago servicioPago;
    @Autowired private ServicioNotificaciones servicioNotificaciones;

    private ModelAndView crearBaseModelAndView(String vista, Authentication aut) {
        ModelAndView mv = new ModelAndView(vista);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());
        return mv;
    }

    // PROFESOR
    @GetMapping("/profesor/reservas")
    public ModelAndView reservasProfesor(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("profesor/reservas", aut);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElseThrow();
        mv.addObject("reservas", servicioReservas.obtenerPorUsuario(usuario));
        mv.addObject("menus", servicioMenus.obtenerTodos());
        return mv;
    }

    @GetMapping("/profesor/pago")
    public ModelAndView mostrarPago(@RequestParam Integer idMenu, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("profesor/pago", aut);
        Menu menu = servicioMenus.buscarPorId(idMenu).orElseThrow();
        mv.addObject("menu", menu);
        mv.addObject("idMenu", idMenu);
        return mv;
    }

    @PostMapping("/profesor/reservas/nueva")
    public ModelAndView crearReserva(
            @RequestParam Integer idMenu,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String numeroTarjeta,
            @RequestParam(required = false) String caducidad,
            @RequestParam(required = false) String cvv,
            @RequestParam(required = false) String telefono,
            Authentication aut,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElseThrow();
        Menu menu = servicioMenus.buscarPorId(idMenu).orElseThrow();

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMenu(menu);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setPrecioPagado(menu.getPrecio());
        reserva.setEstado("PENDIENTE");
        reserva.setCodigoRecogida(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        reserva.setAsistido(false);
        reserva.setMetodoPago(metodoPago);

        if ("CAJA".equalsIgnoreCase(metodoPago)) {
            reserva.setEstadoPago("PENDIENTE");
            Reserva reservaGuardada = repositorioReserva.save(reserva);

            notificarReservaCreada(usuario, menu, reservaGuardada);

            redirectAttributes.addFlashAttribute("exito", "Reserva creada. Recuerda pagar en caja al recoger tu pedido. Tu código es: " + reservaGuardada.getCodigoRecogida());
            return new ModelAndView("redirect:/profesor/reservas");
        } else if ("TARJETA".equalsIgnoreCase(metodoPago) || "BIZUM".equalsIgnoreCase(metodoPago)) {
            ResultadoPago resultado = servicioPago.procesarPago(metodoPago, numeroTarjeta, caducidad, cvv, telefono);
            if (resultado.isExito()) {
                reserva.setEstadoPago("PAGADO");
                Reserva reservaGuardada = repositorioReserva.save(reserva);

                notificarReservaCreada(usuario, menu, reservaGuardada);

                redirectAttributes.addFlashAttribute("exito", "¡Pago realizado! Tu reserva está confirmada. Código de recogida: " + reservaGuardada.getCodigoRecogida());
                return new ModelAndView("redirect:/profesor/reservas");
            } else {
                redirectAttributes.addFlashAttribute("error", "Pago fallido: " + resultado.getMensaje() + ". Inténtalo de nuevo o elige pagar en caja.");
                return new ModelAndView("redirect:/profesor/reservas");
            }
        }

        redirectAttributes.addFlashAttribute("error", "Método de pago no soportado.");
        return new ModelAndView("redirect:/profesor/reservas");
    }

    private void notificarReservaCreada(Usuario usuario, Menu menu, Reserva reservaGuardada) {
        String titulo = "Nueva reserva de " + usuario.getNombre();
        String mensaje = "El profesor " + usuario.getNombre() + " ha reservado el menú del "
                + menu.getFecha() + ". Código: " + reservaGuardada.getCodigoRecogida();
        servicioNotificaciones.notificarARol("COCINERO", Notificacion.NUEVA_RESERVA, titulo, mensaje, "/cocinero/reservas");
        servicioNotificaciones.notificarARol("ADMIN", Notificacion.NUEVA_RESERVA, titulo, mensaje, "/cocinero/reservas");
    }

    @PostMapping("/profesor/reservas/{id}/cancelar")
    public ModelAndView cancelarReserva(@PathVariable Integer id, Authentication aut, RedirectAttributes redirectAttributes) {
        servicioReservas.cancelarReserva(id, aut.getName());
        redirectAttributes.addFlashAttribute("exito", "Reserva cancelada.");
        return new ModelAndView("redirect:/profesor/reservas");
    }

    // COCINERO
    @GetMapping("/cocinero/reservas")
    public ModelAndView reservasCocinero(@RequestParam(required = false) String fecha, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/reservas", aut);
        if (fecha != null && !fecha.trim().isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(fecha);
                mv.addObject("reservas", repositorioReserva.findByFechaReserva(localDate));
                mv.addObject("fechaFiltro", fecha);
            } catch (Exception e) {
                mv.addObject("reservas", servicioReservas.obtenerTodas());
            }
        } else {
            mv.addObject("reservas", servicioReservas.obtenerTodas());
        }
        return mv;
    }

    @PostMapping("/cocinero/reservas/{id}/estado")
    public ModelAndView cambiarEstadoCocinero(@PathVariable Integer id, @RequestParam String estado, RedirectAttributes redirectAttributes) {
        servicioReservas.cambiarEstado(id, estado);
        redirectAttributes.addFlashAttribute("exito", "Estado de reserva actualizado.");
        return new ModelAndView("redirect:/cocinero/reservas");
    }

    @PostMapping("/cocinero/reservas/{id}/pago")
    public ModelAndView registrarPagoCaja(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Reserva reserva = repositorioReserva.findById(id).orElse(null);
        if (reserva == null) {
            redirectAttributes.addFlashAttribute("error", "La reserva no existe.");
            return new ModelAndView("redirect:/cocinero/reservas");
        }

        if (!"CAJA".equalsIgnoreCase(reserva.getMetodoPago())) {
            redirectAttributes.addFlashAttribute("error", "Solo se puede registrar el pago de reservas con método de pago EN CAJA.");
            return new ModelAndView("redirect:/cocinero/reservas");
        }

        if ("PAGADO".equalsIgnoreCase(reserva.getEstadoPago())) {
            redirectAttributes.addFlashAttribute("exito", "Esta reserva ya ha sido cobrada.");
            return new ModelAndView("redirect:/cocinero/reservas");
        }

        reserva.setEstadoPago("PAGADO");
        repositorioReserva.save(reserva);

        redirectAttributes.addFlashAttribute("exito", "Pago registrado correctamente para la reserva de " + reserva.getUsuario().getNombre() + ".");
        return new ModelAndView("redirect:/cocinero/reservas");
    }

    // ADMIN
    @GetMapping("/admin/reservas")
    public ModelAndView reservasAdmin(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("admin/reservas", aut);
        mv.addObject("reservas", servicioReservas.obtenerTodas());
        return mv;
    }

    @PostMapping("/admin/reservas/{id}/estado")
    public ModelAndView cambiarEstadoAdmin(@PathVariable Integer id, @RequestParam String estado, RedirectAttributes redirectAttributes) {
        servicioReservas.cambiarEstado(id, estado);
        redirectAttributes.addFlashAttribute("exito", "Estado de reserva actualizado.");
        return new ModelAndView("redirect:/admin/reservas");
    }
}

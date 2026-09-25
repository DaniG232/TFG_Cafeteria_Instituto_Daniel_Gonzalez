package com.instituto.cafeteria.controladores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.instituto.cafeteria.modelo.Notificacion;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.servicios.ServicioNotificaciones;
import com.instituto.cafeteria.servicios.ServicioUsuarios;
import org.springframework.ui.Model;

@Controller
public class ControladorNotificacion {

    @Autowired
    private ServicioNotificaciones servicioNotificaciones;

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @GetMapping("/notificaciones")
    public String verTodas(Authentication aut, Model model) {
        String nombreUsuario = servicioUsuarios.buscarPorEmail(aut.getName())
                .map(Usuario::getNombre)
                .orElse(aut.getName());
        model.addAttribute("notificaciones", servicioNotificaciones.obtenerTodasLasNotificaciones(aut.getName()));
        model.addAttribute("nombreUsuario", nombreUsuario);
        return "notificaciones-todas";
    }

    @GetMapping("/api/notificaciones")
    @ResponseBody
    public Map<String, Object> obtenerNotificaciones(Authentication aut) {
        String email = aut.getName();
        List<Notificacion> notificaciones = servicioNotificaciones.obtenerNotificaciones(email);
        long noLeidas = servicioNotificaciones.contarNoLeidas(email);

        List<Map<String, Object>> listaDTO = notificaciones.stream().map(n -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", n.getIdNotificacion());
            dto.put("tipo", n.getTipo());
            dto.put("titulo", n.getTitulo());
            dto.put("mensaje", n.getMensaje());
            dto.put("leida", n.getLeida());
            dto.put("fechaCreacion", servicioNotificaciones.calcularFechaRelativa(n.getFechaCreacion()));
            dto.put("urlAccion", n.getUrlAccion());
            return dto;
        }).toList();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("noLeidas", noLeidas);
        respuesta.put("notificaciones", listaDTO);
        return respuesta;
    }

    @PostMapping("/api/notificaciones/{id}/leer")
    @ResponseBody
    public Map<String, Object> marcarComoLeida(@PathVariable Integer id, Authentication aut) {
        servicioNotificaciones.marcarComoLeida(id, aut.getName());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ok", true);
        return respuesta;
    }

    @PostMapping("/api/notificaciones/leer-todas")
    @ResponseBody
    public Map<String, Object> marcarTodasComoLeidas(Authentication aut) {
        servicioNotificaciones.marcarTodasComoLeidas(aut.getName());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ok", true);
        return respuesta;
    }

    @DeleteMapping("/api/notificaciones/eliminar-leidas")
    @ResponseBody
    public Map<String, Object> eliminarLeidas(Authentication aut) {
        servicioNotificaciones.eliminarLeidas(aut.getName());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ok", true);
        return respuesta;
    }
}

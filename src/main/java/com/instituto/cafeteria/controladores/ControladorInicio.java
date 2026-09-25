package com.instituto.cafeteria.controladores;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.servicios.ServicioEncuestas;
import com.instituto.cafeteria.servicios.ServicioMenus;
import com.instituto.cafeteria.servicios.ServicioPlatos;
import com.instituto.cafeteria.servicios.ServicioReservas;
import com.instituto.cafeteria.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorInicio {

    @Autowired private ServicioUsuarios servicioUsuarios;
    @Autowired private ServicioReservas servicioReservas;
    @Autowired private ServicioEncuestas servicioEncuestas;
    @Autowired private ServicioPlatos servicioPlatos;
    @Autowired private ServicioMenus servicioMenus;

    private ModelAndView crearBaseModelAndView(String vista, Authentication aut) {
        ModelAndView mv = new ModelAndView(vista);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());
        return mv;
    }

    @GetMapping("/profesor/inicio")
    public ModelAndView inicioProfesor(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("profesor/inicio", aut);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElseThrow();
        
        var reservas = servicioReservas.obtenerPorUsuario(usuario);
        var encuestas = servicioEncuestas.obtenerTodas();
        long encuestasPendientes = encuestas.stream()
                .filter(e -> !servicioEncuestas.haRespondido(usuario.getEmail(), e.getIdEncuesta()))
                .count();
        mv.addObject("totalReservas", reservas.size());
        mv.addObject("misReservas", reservas.size());
        long reservasPendientes = reservas.stream().filter(r -> "PENDIENTE".equals(r.getEstado())).count();
        mv.addObject("reservasPendientes", reservasPendientes);
        mv.addObject("encuestasPendientes", encuestasPendientes);
        mv.addObject("ultimasReservas", reservas.stream().limit(5).toList());
        return mv;
    }

    @GetMapping("/cocinero/inicio")
    public ModelAndView inicioCocinero(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/inicio", aut);
        mv.addObject("platosActivos", servicioPlatos.contarActivos());
        long platosActivos = servicioPlatos.contarActivos();
        mv.addObject("platosActivos", platosActivos);
        mv.addObject("platosTotales", platosActivos);
        mv.addObject("menusTotales", servicioMenus.contarMenus());
        mv.addObject("profesoresTotales", servicioUsuarios.contarProfesores());
        mv.addObject("reservasHoy", servicioReservas.contarReservasHoy());
        mv.addObject("listaReservasHoy", servicioReservas.obtenerReservasHoy());
        return mv;
    }
    
    @GetMapping("/admin/inicio")
    public ModelAndView inicioAdmin(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("admin/inicio", aut);
        mv.addObject("platosActivos", servicioPlatos.contarActivos());
        mv.addObject("menusTotales", servicioMenus.contarMenus());
        mv.addObject("profesoresTotales", servicioUsuarios.contarProfesores());
        mv.addObject("reservasHoy", servicioReservas.contarReservasHoy());
        return mv;
    }
}
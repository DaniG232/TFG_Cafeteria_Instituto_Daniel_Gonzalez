package com.instituto.cafeteria.controladores;

import com.instituto.cafeteria.modelo.Plato;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.servicios.ServicioPlatos;
import com.instituto.cafeteria.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorPlato {

    @Autowired private ServicioPlatos servicioPlatos;
    @Autowired private ServicioUsuarios servicioUsuarios;

    private ModelAndView crearBaseModelAndView(String vista, Authentication aut) {
        ModelAndView mv = new ModelAndView(vista);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());
        return mv;
    }

    @GetMapping("/cocinero/platos")
    public ModelAndView listaPlatos(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/platos", aut);
        mv.addObject("platos", servicioPlatos.obtenerTodos());
        return mv;
    }

    @GetMapping("/cocinero/platos/nuevo")
    public ModelAndView formularioNuevoPlato(Authentication aut) {
        return crearBaseModelAndView("cocinero/platos-nuevo", aut);
    }

    @PostMapping("/cocinero/platos/nuevo")
    public ModelAndView crearPlato(@ModelAttribute Plato plato, RedirectAttributes redirectAttributes) {
        servicioPlatos.guardar(plato);
        redirectAttributes.addFlashAttribute("exito", "Plato creado correctamente.");
        return new ModelAndView("redirect:/cocinero/platos");
    }

    @GetMapping("/cocinero/platos/{id}/editar")
    public ModelAndView formularioEditarPlato(@PathVariable Integer id, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/platos-editar", aut);
        mv.addObject("plato", servicioPlatos.buscarPorId(id).orElseThrow());
        return mv;
    }

    @PostMapping("/cocinero/platos/{id}/editar")
    public ModelAndView actualizarPlato(@PathVariable Integer id, @ModelAttribute Plato plato, RedirectAttributes redirectAttributes) {
        plato.setIdPlato(id);
        servicioPlatos.guardar(plato);
        redirectAttributes.addFlashAttribute("exito", "Plato actualizado correctamente.");
        return new ModelAndView("redirect:/cocinero/platos");
    }

    @PostMapping("/cocinero/platos/{id}/eliminar")
    public ModelAndView eliminarPlato(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        servicioPlatos.eliminar(id);
        redirectAttributes.addFlashAttribute("exito", "Plato eliminado (desactivado) correctamente.");
        return new ModelAndView("redirect:/cocinero/platos");
    }
}

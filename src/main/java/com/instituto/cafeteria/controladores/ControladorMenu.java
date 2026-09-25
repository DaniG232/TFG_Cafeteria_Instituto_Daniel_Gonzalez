package com.instituto.cafeteria.controladores;

import com.instituto.cafeteria.modelo.Menu;
import com.instituto.cafeteria.modelo.Plato;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.servicios.ServicioMenus;
import com.instituto.cafeteria.servicios.ServicioPlatos;
import com.instituto.cafeteria.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ControladorMenu {

    @Autowired private ServicioMenus servicioMenus;
    @Autowired private ServicioPlatos servicioPlatos;
    @Autowired private ServicioUsuarios servicioUsuarios;

    private ModelAndView crearBaseModelAndView(String vista, Authentication aut) {
        ModelAndView mv = new ModelAndView(vista);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());
        return mv;
    }

    private void cargarPlatosPorTipo(ModelAndView mv) {
        List<Plato> activos = servicioPlatos.obtenerActivos();
        mv.addObject("primeros", activos.stream().filter(p -> p.getTipo() == Plato.TipoPlato.PRIMERO).toList());
        mv.addObject("segundos", activos.stream().filter(p -> p.getTipo() == Plato.TipoPlato.SEGUNDO).toList());
        mv.addObject("postres", activos.stream().filter(p -> p.getTipo() == Plato.TipoPlato.POSTRE).toList());
        mv.addObject("bebidas", activos.stream().filter(p -> p.getTipo() == Plato.TipoPlato.BEBIDA).toList());
    }

    private Menu construirMenu(LocalDate fecha, BigDecimal precio, Integer idPrimerPlato, Integer idSegundoPlato, Integer idPostre, Integer idBebida) {
        Menu menu = new Menu();
        menu.setFecha(fecha);
        menu.setPrecio(precio);
        menu.setPrimerPlato(servicioPlatos.buscarPorId(idPrimerPlato).orElseThrow());
        menu.setSegundoPlato(servicioPlatos.buscarPorId(idSegundoPlato).orElseThrow());
        menu.setPostre(servicioPlatos.buscarPorId(idPostre).orElseThrow());
        menu.setBebida(servicioPlatos.buscarPorId(idBebida).orElseThrow());
        return menu;
    }

    // COCINERO
    @GetMapping("/cocinero/menus")
    public ModelAndView listaMenusCocinero(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/menus", aut);
        mv.addObject("menus", servicioMenus.obtenerTodos());
        return mv;
    }

    @GetMapping("/cocinero/menus/nuevo")
    public ModelAndView formularioNuevoMenuCocinero(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/menus-nuevo", aut);
        cargarPlatosPorTipo(mv);
        return mv;
    }

    @PostMapping("/cocinero/menus/nuevo")
    public ModelAndView crearMenuCocinero(@RequestParam LocalDate fecha,
                                          @RequestParam BigDecimal precio,
                                          @RequestParam Integer primerPlato,
                                          @RequestParam Integer segundoPlato,
                                          @RequestParam Integer postre,
                                          @RequestParam Integer bebida,
                                          RedirectAttributes redirectAttributes) {
        Menu menu = construirMenu(fecha, precio, primerPlato, segundoPlato, postre, bebida);
        servicioMenus.guardar(menu);
        redirectAttributes.addFlashAttribute("exito", "Menú creado correctamente.");
        return new ModelAndView("redirect:/cocinero/menus");
    }

    @GetMapping("/cocinero/menus/{id}/editar")
    public ModelAndView formularioEditarMenuCocinero(@PathVariable Integer id, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/menus-editar", aut);
        mv.addObject("menu", servicioMenus.buscarPorId(id).orElseThrow());
        cargarPlatosPorTipo(mv);
        return mv;
    }

    @PostMapping("/cocinero/menus/{id}/editar")
    public ModelAndView actualizarMenuCocinero(@PathVariable Integer id,
                                               @RequestParam LocalDate fecha,
                                               @RequestParam BigDecimal precio,
                                               @RequestParam Integer primerPlato,
                                               @RequestParam Integer segundoPlato,
                                               @RequestParam Integer postre,
                                               @RequestParam Integer bebida,
                                               RedirectAttributes redirectAttributes) {
        Menu menu = construirMenu(fecha, precio, primerPlato, segundoPlato, postre, bebida);
        menu.setIdMenu(id);
        servicioMenus.guardar(menu);
        redirectAttributes.addFlashAttribute("exito", "Menú actualizado correctamente.");
        return new ModelAndView("redirect:/cocinero/menus");
    }

    @PostMapping("/cocinero/menus/{id}/eliminar")
    public ModelAndView eliminarMenuCocinero(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        servicioMenus.eliminar(id);
        redirectAttributes.addFlashAttribute("exito", "Menú eliminado correctamente.");
        return new ModelAndView("redirect:/cocinero/menus");
    }

    // ADMIN
    @GetMapping("/admin/menus")
    public ModelAndView listaMenusAdmin(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("admin/menus", aut);
        mv.addObject("menus", servicioMenus.obtenerTodos());
        return mv;
    }

    @GetMapping("/admin/menus/nuevo")
    public ModelAndView formularioNuevoMenuAdmin(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("admin/menus-nuevo", aut);
        cargarPlatosPorTipo(mv);
        return mv;
    }

    @PostMapping("/admin/menus/nuevo")
    public ModelAndView crearMenuAdmin(@RequestParam LocalDate fecha,
                                       @RequestParam BigDecimal precio,
                                       @RequestParam Integer primerPlato,
                                       @RequestParam Integer segundoPlato,
                                       @RequestParam Integer postre,
                                       @RequestParam Integer bebida,
                                       RedirectAttributes redirectAttributes) {
        Menu menu = construirMenu(fecha, precio, primerPlato, segundoPlato, postre, bebida);
        servicioMenus.guardar(menu);
        redirectAttributes.addFlashAttribute("exito", "Menú creado correctamente.");
        return new ModelAndView("redirect:/admin/menus");
    }

    @GetMapping("/admin/menus/{id}/editar")
    public ModelAndView formularioEditarMenuAdmin(@PathVariable Integer id, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("admin/menus-editar", aut);
        mv.addObject("menu", servicioMenus.buscarPorId(id).orElseThrow());
        cargarPlatosPorTipo(mv);
        return mv;
    }

    @PostMapping("/admin/menus/{id}/editar")
    public ModelAndView actualizarMenuAdmin(@PathVariable Integer id,
                                            @RequestParam LocalDate fecha,
                                            @RequestParam BigDecimal precio,
                                            @RequestParam Integer primerPlato,
                                            @RequestParam Integer segundoPlato,
                                            @RequestParam Integer postre,
                                            @RequestParam Integer bebida,
                                            RedirectAttributes redirectAttributes) {
        Menu menu = construirMenu(fecha, precio, primerPlato, segundoPlato, postre, bebida);
        menu.setIdMenu(id);
        servicioMenus.guardar(menu);
        redirectAttributes.addFlashAttribute("exito", "Menú actualizado correctamente.");
        return new ModelAndView("redirect:/admin/menus");
    }

    @PostMapping("/admin/menus/{id}/eliminar")
    public ModelAndView eliminarMenuAdmin(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        servicioMenus.eliminar(id);
        redirectAttributes.addFlashAttribute("exito", "Menú eliminado correctamente.");
        return new ModelAndView("redirect:/admin/menus");
    }

    // PROFESOR
    @GetMapping("/profesor/menus")
    public ModelAndView listaMenusProfesor(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("profesor/menus", aut);
        mv.addObject("menus", servicioMenus.obtenerTodos());
        return mv;
    }
}

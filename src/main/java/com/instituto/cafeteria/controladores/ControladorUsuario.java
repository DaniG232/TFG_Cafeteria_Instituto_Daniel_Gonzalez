package com.instituto.cafeteria.controladores;

import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.servicios.ServicioUsuarios;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class ControladorUsuario {

    private final ServicioUsuarios servicioUsuarios;
    private final PasswordEncoder passwordEncoder;
    private final com.instituto.cafeteria.servicios.ServicioNotificaciones servicioNotificaciones;

    public ControladorUsuario(ServicioUsuarios servicioUsuarios, PasswordEncoder passwordEncoder, com.instituto.cafeteria.servicios.ServicioNotificaciones servicioNotificaciones) {
        this.servicioUsuarios = servicioUsuarios;
        this.passwordEncoder = passwordEncoder;
        this.servicioNotificaciones = servicioNotificaciones;
    }

    @GetMapping("/")
    public ModelAndView raiz(Authentication aut) {
        if (aut != null && aut.isAuthenticated()) {
            for (GrantedAuthority authority : aut.getAuthorities()) {
                String rol = authority.getAuthority();
                if (rol.equals("ADMIN")) return new ModelAndView("redirect:/admin/inicio");
                if (rol.equals("COCINERO")) return new ModelAndView("redirect:/cocinero/inicio");
                if (rol.equals("PROFESOR")) return new ModelAndView("redirect:/profesor/inicio");
            }
        }
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/denegado")
    public ModelAndView accesoDenegado() {
        return new ModelAndView("denegado");
    }

    @GetMapping("/error")
    public ModelAndView error() {
        return new ModelAndView("error");
    }

    @GetMapping("/registro")
    public ModelAndView registroForm() {
        return new ModelAndView("registro");
    }

    @PostMapping("/registro")
    public ModelAndView procesarRegistro(@RequestParam String nombre,
                                         @RequestParam String email,
                                         @RequestParam String contrasena) {

        if (servicioUsuarios.buscarPorEmail(email).isPresent()) {
            ModelAndView mv = new ModelAndView("registro");
            mv.addObject("error", "El email ya está registrado.");
            return mv;
        }

        Usuario nuevo = new Usuario();
        nuevo.setEmail(email);
        nuevo.setNombre(nombre);
        nuevo.setContrasena(contrasena); // registrarUsuario lo codificará

        servicioUsuarios.registrarUsuario(nuevo, Usuario.Rol.PROFESOR);

        return new ModelAndView("redirect:/login?registro=exito");
    }

    @GetMapping("/admin/usuarios")
    public ModelAndView listaUsuarios(Authentication aut) {
        ModelAndView mv = new ModelAndView("admin/usuarios");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());

        mv.addObject("usuarios", servicioUsuarios.obtenerTodos());
        return mv;
    }

    @PostMapping("/admin/usuarios/{email}/rol")
    public ModelAndView cambiarRol(@PathVariable String email,
                                    @RequestParam Usuario.Rol rol,
                                    RedirectAttributes redirectAttributes) {
        servicioUsuarios.cambiarRol(email, rol);
        redirectAttributes.addFlashAttribute("exito", "Rol actualizado correctamente.");
        return new ModelAndView("redirect:/admin/usuarios");
    }

    @PostMapping("/admin/usuarios/{email}/estado")
    public ModelAndView cambiarEstado(@PathVariable String email,
                                      @RequestParam boolean estado,
                                      RedirectAttributes redirectAttributes) {
        servicioUsuarios.cambiarEstado(email, estado);
        redirectAttributes.addFlashAttribute("exito", "Estado actualizado correctamente.");
        return new ModelAndView("redirect:/admin/usuarios");
    }

    @GetMapping("/admin/usuarios/nuevo")
    public ModelAndView formularioNuevoUsuario(Authentication aut) {
        ModelAndView mv = new ModelAndView("admin/usuarios-nuevo");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());

        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());

        return mv;
    }

    @PostMapping("/admin/usuarios/nuevo")
    public ModelAndView crearUsuario(@RequestParam String nombre,
                                     @RequestParam String email,
                                     @RequestParam String contrasena,
                                     @RequestParam Usuario.Rol rol,
                                     RedirectAttributes redirectAttributes) {

        if (servicioUsuarios.buscarPorEmail(email).isPresent()) {
            ModelAndView mv = new ModelAndView("admin/usuarios-nuevo");
            mv.addObject("error", "El email ya está registrado.");
            return mv;
        }

        Usuario nuevo = new Usuario();
        nuevo.setEmail(email);
        nuevo.setNombre(nombre);
        nuevo.setContrasena(passwordEncoder.encode(contrasena));
        nuevo.setRol(rol);
        nuevo.setFechaRegistro(LocalDate.now());
        nuevo.setEstado(true);

        servicioUsuarios.guardarUsuario(nuevo);

        redirectAttributes.addFlashAttribute("exito", "Usuario creado con éxito.");
        return new ModelAndView("redirect:/admin/usuarios");
    }

    @GetMapping("/admin/avisos")
    public ModelAndView formularioAvisos(Authentication aut) {
        ModelAndView mv = new ModelAndView("admin/avisos");
        mv.addObject("usuarioAutenticado", true);
        mv.addObject("emailUsuario", aut.getName());
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());
        return mv;
    }

    @PostMapping("/admin/avisos")
    public ModelAndView enviarAviso(@RequestParam String titulo,
                                   @RequestParam String mensaje,
                                   RedirectAttributes redirectAttributes) {
        servicioNotificaciones.notificarARol("PROFESOR", "AVISO_ADMIN", titulo, mensaje, null);
        redirectAttributes.addFlashAttribute("exito", "Aviso enviado correctamente a todos los profesores.");
        return new ModelAndView("redirect:/admin/avisos");
    }
}

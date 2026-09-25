package com.instituto.cafeteria.controladores;

import com.instituto.cafeteria.modelo.Encuesta;
import com.instituto.cafeteria.modelo.Usuario;
import com.instituto.cafeteria.servicios.ServicioEncuestas;
import com.instituto.cafeteria.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ControladorEncuesta {

    @Autowired private ServicioEncuestas servicioEncuestas;
    @Autowired private ServicioUsuarios servicioUsuarios;

    private ModelAndView crearBaseModelAndView(String vista, Authentication aut) {
        ModelAndView mv = new ModelAndView(vista);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElse(null);
        mv.addObject("nombreUsuario", usuario != null ? usuario.getNombre() : aut.getName());
        return mv;
    }

    // COCINERO
    @GetMapping("/cocinero/encuestas")
    public ModelAndView listaEncuestasCocinero(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/encuestas", aut);
        mv.addObject("encuestas", servicioEncuestas.obtenerTodas());
        return mv;
    }

    @GetMapping("/cocinero/encuestas/nueva")
    public ModelAndView formularioNuevaEncuestaCocinero(Authentication aut) {
        return crearBaseModelAndView("cocinero/encuestas-nueva", aut);
    }

    @PostMapping("/cocinero/encuestas/nueva")
    public ModelAndView crearEncuestaCocinero(@RequestParam String titulo,
                                              @RequestParam(required = false) String descripcion,
                                              @RequestParam("preguntas") List<String> preguntas,
                                              RedirectAttributes redirectAttributes) {
        Encuesta encuesta = new Encuesta();
        encuesta.setTitulo(titulo);
        encuesta.setDescripcion(descripcion);
        List<String> preguntasValidas = preguntas.stream().filter(p -> p != null && !p.trim().isEmpty()).toList();
        servicioEncuestas.crearEncuestaConPreguntas(encuesta, preguntasValidas);
        redirectAttributes.addFlashAttribute("exito", "Encuesta creada correctamente.");
        return new ModelAndView("redirect:/cocinero/encuestas");
    }

    @PostMapping("/cocinero/encuestas/{id}/eliminar")
    public ModelAndView eliminarEncuestaCocinero(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        servicioEncuestas.eliminarEncuesta(id);
        redirectAttributes.addFlashAttribute("exito", "Encuesta eliminada.");
        return new ModelAndView("redirect:/cocinero/encuestas");
    }

    @GetMapping("/cocinero/encuestas/{id}/respuestas")
    public ModelAndView verRespuestasCocinero(@PathVariable Integer id, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("cocinero/encuestas-respuestas", aut);
        Encuesta encuesta = servicioEncuestas.buscarPorId(id).orElseThrow();
        mv.addObject("encuesta", encuesta);
        
        // Cargar respuestas por pregunta
        Map<Integer, List<String>> respuestasPorPregunta = encuesta.getPreguntas().stream()
            .collect(Collectors.toMap(
                p -> p.getIdPregunta(),
                p -> servicioEncuestas.obtenerRespuestasPorPregunta(p.getIdPregunta()).stream().map(r -> r.getRespuesta() + " (" + r.getUsuario().getNombre() + ")").toList()
            ));
        mv.addObject("respuestasPorPregunta", respuestasPorPregunta);
        
        return mv;
    }

    // ADMIN (Reutiliza la misma lógica)
    @GetMapping("/admin/encuestas")
    public ModelAndView listaEncuestasAdmin(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("admin/encuestas", aut);
        mv.addObject("encuestas", servicioEncuestas.obtenerTodas());
        return mv;
    }

    @GetMapping("/admin/encuestas/nueva")
    public ModelAndView formularioNuevaEncuestaAdmin(Authentication aut) {
        return crearBaseModelAndView("admin/encuestas-nueva", aut);
    }

    @PostMapping("/admin/encuestas/nueva")
    public ModelAndView crearEncuestaAdmin(@RequestParam String titulo,
                                           @RequestParam(required = false) String descripcion,
                                           @RequestParam("preguntas") List<String> preguntas,
                                           RedirectAttributes redirectAttributes) {
        Encuesta encuesta = new Encuesta();
        encuesta.setTitulo(titulo);
        encuesta.setDescripcion(descripcion);
        List<String> preguntasValidas = preguntas.stream().filter(p -> p != null && !p.trim().isEmpty()).toList();
        servicioEncuestas.crearEncuestaConPreguntas(encuesta, preguntasValidas);
        redirectAttributes.addFlashAttribute("exito", "Encuesta creada correctamente.");
        return new ModelAndView("redirect:/admin/encuestas");
    }

    @PostMapping("/admin/encuestas/{id}/eliminar")
    public ModelAndView eliminarEncuestaAdmin(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        servicioEncuestas.eliminarEncuesta(id);
        redirectAttributes.addFlashAttribute("exito", "Encuesta eliminada.");
        return new ModelAndView("redirect:/admin/encuestas");
    }

    @GetMapping("/admin/encuestas/{id}/respuestas")
    public ModelAndView verRespuestasAdmin(@PathVariable Integer id, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("admin/encuestas-respuestas", aut);
        Encuesta encuesta = servicioEncuestas.buscarPorId(id).orElseThrow();
        mv.addObject("encuesta", encuesta);
        Map<Integer, List<String>> respuestasPorPregunta = encuesta.getPreguntas().stream()
            .collect(Collectors.toMap(
                p -> p.getIdPregunta(),
                p -> servicioEncuestas.obtenerRespuestasPorPregunta(p.getIdPregunta()).stream().map(r -> r.getRespuesta() + " (" + r.getUsuario().getNombre() + ")").toList()
            ));
        mv.addObject("respuestasPorPregunta", respuestasPorPregunta);
        return mv;
    }

    // PROFESOR
    @GetMapping("/profesor/encuestas")
    public ModelAndView listaEncuestasProfesor(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("profesor/encuestas", aut);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElseThrow();
        List<Encuesta> encuestas = servicioEncuestas.obtenerTodas();
        
        List<Encuesta> pendientes = encuestas.stream()
            .filter(e -> !servicioEncuestas.haRespondido(usuario.getEmail(), e.getIdEncuesta()))
            .toList();
            
        mv.addObject("pendientes", pendientes);
        return mv;
    }

    @GetMapping("/profesor/encuestas/{id}/responder")
    public ModelAndView formularioResponderEncuesta(@PathVariable Integer id, Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("profesor/encuestas-responder", aut);
        Encuesta encuesta = servicioEncuestas.buscarPorId(id).orElseThrow();
        mv.addObject("encuesta", encuesta);
        return mv;
    }

    @PostMapping("/profesor/encuestas/{id}/responder")
    public ModelAndView responderEncuesta(@PathVariable Integer id, @RequestParam Map<String, String> allParams, Authentication aut, RedirectAttributes redirectAttributes) {
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElseThrow();
        Encuesta encuesta = servicioEncuestas.buscarPorId(id).orElseThrow();
        
        Map<Integer, String> respuestas = allParams.entrySet().stream()
            .filter(e -> e.getKey().startsWith("respuesta_"))
            .collect(Collectors.toMap(
                e -> Integer.parseInt(e.getKey().replace("respuesta_", "")),
                Map.Entry::getValue
            ));

        servicioEncuestas.guardarRespuestas(usuario, encuesta, respuestas);
        redirectAttributes.addFlashAttribute("exito", "Encuesta respondida correctamente. ¡Gracias!");
        return new ModelAndView("redirect:/profesor/encuestas");
    }

    @GetMapping("/profesor/encuestas/mis-respuestas")
    public ModelAndView misRespuestas(Authentication aut) {
        ModelAndView mv = crearBaseModelAndView("profesor/encuestas-mis-respuestas", aut);
        Usuario usuario = servicioUsuarios.buscarPorEmail(aut.getName()).orElseThrow();
        mv.addObject("rellenas", servicioEncuestas.obtenerRespondidasPorUsuario(usuario));
        return mv;
    }
}

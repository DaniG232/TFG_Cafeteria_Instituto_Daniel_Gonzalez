package com.instituto.cafeteria.servicios;

import com.instituto.cafeteria.modelo.*;
import com.instituto.cafeteria.repositorios.RepositorioEncuesta;
import com.instituto.cafeteria.repositorios.RepositorioRellena;
import com.instituto.cafeteria.repositorios.RepositorioResponde;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServicioEncuestas {

    @Autowired
    private RepositorioEncuesta repositorioEncuesta;

    @Autowired
    private RepositorioRellena repositorioRellena;

    @Autowired
    private RepositorioResponde repositorioResponde;

    @Autowired
    private ServicioNotificaciones servicioNotificaciones;

    public List<Encuesta> obtenerTodas() {
        return repositorioEncuesta.findAll();
    }

    public Optional<Encuesta> buscarPorId(Integer id) {
        return repositorioEncuesta.findById(id);
    }

    @Transactional
    public Encuesta crearEncuestaConPreguntas(Encuesta encuesta, List<String> textosPreguntas) {
        encuesta.setFechaCreacion(LocalDate.now());
        
        List<Pregunta> preguntas = textosPreguntas.stream().map(texto -> {
            Pregunta p = new Pregunta();
            p.setTexto(texto);
            p.setEncuesta(encuesta);
            return p;
        }).toList();
        
        encuesta.setPreguntas(preguntas);
        Encuesta encuestaGuardada = repositorioEncuesta.save(encuesta);

        // Notificar a todos los PROFESOR
        servicioNotificaciones.notificarARol("PROFESOR",
                Notificacion.NUEVA_ENCUESTA,
                "Nueva encuesta disponible: " + encuestaGuardada.getTitulo(),
                "Se ha publicado una nueva encuesta. ¡Tu opinión es importante!",
                "/profesor/encuestas");

        return encuestaGuardada;
    }

    public void eliminarEncuesta(Integer id) {
        repositorioEncuesta.deleteById(id);
    }

    public List<Rellena> obtenerRespondidasPorUsuario(Usuario usuario) {
        return repositorioRellena.findByUsuario(usuario);
    }

    public boolean haRespondido(String email, Integer idEncuesta) {
        return repositorioRellena.findByUsuarioEmailAndEncuestaIdEncuesta(email, idEncuesta).isPresent();
    }

    @Transactional
    public void guardarRespuestas(Usuario usuario, Encuesta encuesta, Map<Integer, String> respuestas) {
        // Guardar respuestas
        for (Map.Entry<Integer, String> entry : respuestas.entrySet()) {
            Integer idPregunta = entry.getKey();
            String textoRespuesta = entry.getValue();

            encuesta.getPreguntas().stream()
                    .filter(p -> p.getIdPregunta().equals(idPregunta))
                    .findFirst()
                    .ifPresent(pregunta -> {
                        Responde responde = new Responde();
                        responde.setUsuario(usuario);
                        responde.setPregunta(pregunta);
                        responde.setRespuesta(textoRespuesta);
                        repositorioResponde.save(responde);
                    });
        }

        // Marcar como rellenada
        Rellena rellena = new Rellena();
        rellena.setUsuario(usuario);
        rellena.setEncuesta(encuesta);
        rellena.setFechaRespuesta(LocalDate.now());
        repositorioRellena.save(rellena);

        // Notificar a todos los COCINERO y ADMIN
        String titulo = "Nueva respuesta en " + encuesta.getTitulo();
        String mensaje = "El profesor " + usuario.getNombre() + " ha respondido la encuesta '" + encuesta.getTitulo() + "'.";
        String urlAccion = "/cocinero/encuestas/" + encuesta.getIdEncuesta() + "/respuestas";
        servicioNotificaciones.notificarARol("COCINERO", Notificacion.NUEVA_RESPUESTA_ENCUESTA, titulo, mensaje, urlAccion);
        servicioNotificaciones.notificarARol("ADMIN", Notificacion.NUEVA_RESPUESTA_ENCUESTA, titulo, mensaje, urlAccion);
    }

    public List<Responde> obtenerRespuestasPorPregunta(Integer idPregunta) {
        return repositorioResponde.findByPreguntaIdPregunta(idPregunta);
    }
}

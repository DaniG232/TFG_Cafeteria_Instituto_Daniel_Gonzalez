package com.instituto.cafeteria.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rellena")
@IdClass(RellenaId.class)
public class Rellena {

    @Id
    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    private Usuario usuario;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_encuesta", referencedColumnName = "id_encuesta")
    private Encuesta encuesta;

    @Column(name = "fecha_respuesta")
    private LocalDate fechaRespuesta;

    public Rellena() {}

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Encuesta getEncuesta() { return encuesta; }
    public void setEncuesta(Encuesta encuesta) { this.encuesta = encuesta; }

    public LocalDate getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDate fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}

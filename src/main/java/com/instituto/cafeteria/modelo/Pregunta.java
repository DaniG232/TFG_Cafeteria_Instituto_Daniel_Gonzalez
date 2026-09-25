package com.instituto.cafeteria.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "pregunta")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Integer idPregunta;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @ManyToOne
    @JoinColumn(name = "id_encuesta", nullable = false)
    @JsonBackReference
    private Encuesta encuesta;

    public Pregunta() {}

    public Integer getIdPregunta() { return idPregunta; }
    public void setIdPregunta(Integer idPregunta) { this.idPregunta = idPregunta; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Encuesta getEncuesta() { return encuesta; }
    public void setEncuesta(Encuesta encuesta) { this.encuesta = encuesta; }
}

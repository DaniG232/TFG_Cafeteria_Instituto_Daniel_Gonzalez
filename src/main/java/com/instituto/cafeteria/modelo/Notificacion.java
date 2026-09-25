package com.instituto.cafeteria.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(name = "email_destinatario", nullable = false, length = 150)
    private String emailDestinatario;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "leida", columnDefinition = "boolean default false")
    private Boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "url_accion", length = 255)
    private String urlAccion;

    public static final String RESERVA_LISTA = "RESERVA_LISTA";
    public static final String NUEVA_ENCUESTA = "NUEVA_ENCUESTA";
    public static final String AVISO_ADMIN = "AVISO_ADMIN";
    public static final String NUEVA_RESERVA = "NUEVA_RESERVA";
    public static final String NUEVA_RESPUESTA_ENCUESTA = "NUEVA_RESPUESTA_ENCUESTA";
    public static final String NUEVO_USUARIO = "NUEVO_USUARIO";

    public Notificacion() {}

    public Integer getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Integer idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getEmailDestinatario() { return emailDestinatario; }
    public void setEmailDestinatario(String emailDestinatario) { this.emailDestinatario = emailDestinatario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUrlAccion() { return urlAccion; }
    public void setUrlAccion(String urlAccion) { this.urlAccion = urlAccion; }
}

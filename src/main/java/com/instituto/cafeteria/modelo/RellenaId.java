package com.instituto.cafeteria.modelo;

import java.io.Serializable;
import java.util.Objects;

public class RellenaId implements Serializable {
    
    private String usuario;
    private Integer encuesta;

    public RellenaId() {}

    public RellenaId(String usuario, Integer encuesta) {
        this.usuario = usuario;
        this.encuesta = encuesta;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Integer getEncuesta() { return encuesta; }
    public void setEncuesta(Integer encuesta) { this.encuesta = encuesta; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RellenaId rellenaId = (RellenaId) o;
        return Objects.equals(usuario, rellenaId.usuario) && Objects.equals(encuesta, rellenaId.encuesta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, encuesta);
    }
}

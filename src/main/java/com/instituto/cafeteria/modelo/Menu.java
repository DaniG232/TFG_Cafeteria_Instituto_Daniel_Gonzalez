package com.instituto.cafeteria.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu")
    private Integer idMenu;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "precio", nullable = false, precision = 6, scale = 2)
    private BigDecimal precio;

    @ManyToOne
    @JoinColumn(name = "id_primer_plato", nullable = false)
    private Plato primerPlato;

    @ManyToOne
    @JoinColumn(name = "id_segundo_plato", nullable = false)
    private Plato segundoPlato;

    @ManyToOne
    @JoinColumn(name = "id_postre", nullable = false)
    private Plato postre;

    @ManyToOne
    @JoinColumn(name = "id_bebida", nullable = false)
    private Plato bebida;

    public Menu() {}

    public Integer getIdMenu() { return idMenu; }
    public void setIdMenu(Integer idMenu) { this.idMenu = idMenu; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Plato getPrimerPlato() { return primerPlato; }
    public void setPrimerPlato(Plato primerPlato) { this.primerPlato = primerPlato; }

    public Plato getSegundoPlato() { return segundoPlato; }
    public void setSegundoPlato(Plato segundoPlato) { this.segundoPlato = segundoPlato; }

    public Plato getPostre() { return postre; }
    public void setPostre(Plato postre) { this.postre = postre; }

    public Plato getBebida() { return bebida; }
    public void setBebida(Plato bebida) { this.bebida = bebida; }
}

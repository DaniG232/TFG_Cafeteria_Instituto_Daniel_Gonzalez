package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Reserva;
import com.instituto.cafeteria.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepositorioReserva extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByUsuarioOrderByFechaReservaDesc(Usuario usuario);
    List<Reserva> findByFechaReserva(LocalDate fechaReserva);
    long countByFechaReserva(LocalDate fechaReserva);
}

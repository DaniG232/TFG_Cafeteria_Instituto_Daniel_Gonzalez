package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioPregunta extends JpaRepository<Pregunta, Integer> {
    List<Pregunta> findByEncuestaIdEncuesta(Integer idEncuesta);
}

package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Responde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioResponde extends JpaRepository<Responde, Integer> {
    List<Responde> findByPreguntaIdPregunta(Integer idPregunta);
}

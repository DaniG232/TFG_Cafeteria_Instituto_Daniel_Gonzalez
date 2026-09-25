package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioEncuesta extends JpaRepository<Encuesta, Integer> {
}

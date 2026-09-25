package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioPlato extends JpaRepository<Plato, Integer> {
    List<Plato> findByActivoTrue();
    long countByActivoTrue();
}

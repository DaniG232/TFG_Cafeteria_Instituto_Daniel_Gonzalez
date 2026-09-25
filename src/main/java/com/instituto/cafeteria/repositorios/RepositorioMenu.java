package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioMenu extends JpaRepository<Menu, Integer> {
}

package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Rellena;
import com.instituto.cafeteria.modelo.RellenaId;
import com.instituto.cafeteria.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioRellena extends JpaRepository<Rellena, RellenaId> {
    List<Rellena> findByUsuario(Usuario usuario);
    Optional<Rellena> findByUsuarioEmailAndEncuestaIdEncuesta(String email, Integer idEncuesta);
}

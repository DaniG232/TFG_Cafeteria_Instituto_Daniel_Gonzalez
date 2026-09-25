package com.instituto.cafeteria.repositorios;

import com.instituto.cafeteria.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, String> {
    List<Usuario> findByRolAndEstadoTrue(Usuario.Rol rol);
    List<Usuario> findByEstadoTrue();
}

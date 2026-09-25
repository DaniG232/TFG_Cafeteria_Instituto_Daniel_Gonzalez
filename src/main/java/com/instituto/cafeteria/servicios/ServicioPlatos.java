package com.instituto.cafeteria.servicios;

import com.instituto.cafeteria.modelo.Plato;
import com.instituto.cafeteria.repositorios.RepositorioPlato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioPlatos {

    @Autowired
    private RepositorioPlato repositorioPlato;

    public List<Plato> obtenerTodos() {
        return repositorioPlato.findAll();
    }

    public List<Plato> obtenerActivos() {
        return repositorioPlato.findByActivoTrue();
    }

    public Optional<Plato> buscarPorId(Integer id) {
        return repositorioPlato.findById(id);
    }

    public Plato guardar(Plato plato) {
        if (plato.getActivo() == null) {
            plato.setActivo(true);
        }
        return repositorioPlato.save(plato);
    }

    public void eliminar(Integer id) {
        repositorioPlato.findById(id).ifPresent(plato -> {
            plato.setActivo(false);
            repositorioPlato.save(plato);
        });
    }

    public long contarActivos() {
        return repositorioPlato.countByActivoTrue();
    }
}

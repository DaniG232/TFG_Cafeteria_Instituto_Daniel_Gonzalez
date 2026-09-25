package com.instituto.cafeteria.servicios;

import com.instituto.cafeteria.modelo.Menu;
import com.instituto.cafeteria.repositorios.RepositorioMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioMenus {

    @Autowired
    private RepositorioMenu repositorioMenu;

    public List<Menu> obtenerTodos() {
        return repositorioMenu.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fecha"));
    }

    public Optional<Menu> buscarPorId(Integer id) {
        return repositorioMenu.findById(id);
    }

    public Menu guardar(Menu menu) {
        return repositorioMenu.save(menu);
    }

    public void eliminar(Integer id) {
        repositorioMenu.deleteById(id);
    }

    public long contarMenus() {
        return repositorioMenu.count();
    }
}

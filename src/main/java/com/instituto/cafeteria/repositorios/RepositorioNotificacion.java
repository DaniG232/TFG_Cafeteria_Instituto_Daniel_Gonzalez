package com.instituto.cafeteria.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.instituto.cafeteria.modelo.Notificacion;

@Repository
public interface RepositorioNotificacion extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByEmailDestinatarioOrderByFechaCreacionDesc(String email);

    List<Notificacion> findByEmailDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(String email);

    long countByEmailDestinatarioAndLeidaFalse(String email);

    @Modifying
    @Transactional
    void deleteByEmailDestinatarioAndLeidaTrue(String email);
}

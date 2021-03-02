package com.register.employe.infraestructure.repository;

import com.register.employe.infraestructure.model.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<UsuarioEntity, Long> {

    UsuarioEntity findByUsuario(String usuario);
    UsuarioEntity findByCorreoElectronico(String correoElectronico);
}

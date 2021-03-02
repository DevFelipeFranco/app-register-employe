package com.register.employe.infraestructure.service;

import com.register.employe.infraestructure.model.UserPrincipal;
import com.register.employe.infraestructure.model.UsuarioEntity;
import com.register.employe.infraestructure.repository.UsuarioRepositorio;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Qualifier("userDetailsService")
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    public static final String NO_SE_ENCONTRO_EL_USUARIO = "No se encontro el usuario: ";
    private final UsuarioRepositorio usuarioRepositorio;
    private LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        UsuarioEntity usuarioEncontrado = usuarioRepositorio.findByUsuario(usuario);
        if (usuarioEncontrado == null) {
            log.error(NO_SE_ENCONTRO_EL_USUARIO + usuario);
            throw new UsernameNotFoundException(NO_SE_ENCONTRO_EL_USUARIO + usuario);
        } else {
            validateLoginAttempt(usuarioEncontrado);
            usuarioEncontrado.setUltimaFechaIngresoVisualizacion(usuarioEncontrado.getUltimaFechaIngreso());
            usuarioEncontrado.setUltimaFechaIngreso(LocalDateTime.now());
            usuarioRepositorio.save(usuarioEncontrado);
            UserPrincipal userPrincipal = new UserPrincipal(usuarioEncontrado);
            log.info("Retorna el usuario encontrado por el usuario: " + usuario);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(UsuarioEntity user) {
        if(user.getSnBloqueado()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsuario())) {
                user.setSnBloqueado(false);
            } else {
                user.setSnBloqueado(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsuario());
        }
    }
}

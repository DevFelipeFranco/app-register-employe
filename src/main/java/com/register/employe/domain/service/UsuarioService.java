package com.register.employe.domain.service;

import com.register.employe.domain.constant.SecurityConstant;
import com.register.employe.domain.enumeration.Role;
import com.register.employe.domain.exception.EmailExistException;
import com.register.employe.domain.exception.UserNotFoundException;
import com.register.employe.domain.exception.UsernameExisteException;
import com.register.employe.infraestructure.model.UserPrincipal;
import com.register.employe.infraestructure.model.UsuarioEntity;
import com.register.employe.infraestructure.repository.UsuarioRepositorio;
import com.register.employe.infraestructure.service.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.register.employe.domain.constant.UsuarioConstant.*;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;


    public UsuarioEntity registrar(String nombres, String apellidos, String usuario, String email) throws UserNotFoundException, UsernameExisteException, EmailExistException {
        validateNuevoUsuarioYEmail(StringUtils.EMPTY, usuario, email);

        String clave = generarClave();
        String claveCifrada = cifrarClave(clave);

        UsuarioEntity usuarioEntity = UsuarioEntity.builder()
                .idUsuario(generarUsuarioId())
                .nombres(nombres)
                .apellidos(apellidos)
                .usuario(usuario)
                .correoElectronico(email)
                .fechaRegistro(LocalDateTime.now())
                .clave(claveCifrada)
                .snActivo(true)
                .snBloqueado(true)
                .role(Role.ROLE_USER.name())
                .autorizacion(Role.ROLE_USER.getAuthorities())
                .imagenPerfilUrl(getImagenPerfilTemporar())
                .build();

        UsuarioEntity usuarioRegistado = usuarioRepositorio.save(usuarioEntity);
        log.info("Nuevo usuario clave: " + clave);

        return usuarioRegistado;
    }

    private String getImagenPerfilTemporar() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(RUTA_IMAGEN_PERFIL_POR_DEFECTO).toUriString();
    }

    private String cifrarClave(String clave) {
        return passwordEncoder.encode(clave);
    }

    private String generarClave() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generarUsuarioId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private UsuarioEntity validateNuevoUsuarioYEmail(String actualNombreUsuario, String nuevoUsuario, String nuevoEmail) throws UserNotFoundException, UsernameExisteException, EmailExistException {
        UsuarioEntity usuarioPorNuevoNombreUsuario = buscarUsuarioPorNombreUsuario(nuevoUsuario);
        UsuarioEntity usuarioPorNuevoEmail = buscarUsuarioPorEmail(nuevoEmail);

        if (StringUtils.isNoneBlank(actualNombreUsuario)) {
            UsuarioEntity actualUsuario = buscarUsuarioPorNombreUsuario(actualNombreUsuario);
            if (actualUsuario == null) {
                throw new UserNotFoundException(NO_SE_ENCONTRO_EL_USUARIO_POR + actualNombreUsuario);
            }

            if (usuarioPorNuevoNombreUsuario != null && !actualUsuario.getIdUsuario().equals(usuarioPorNuevoNombreUsuario.getId())) {
                throw new UsernameExisteException(EL_USUARIO_YA_SE_ENCUENTRA_REGISTRADO);
            }

            if (usuarioPorNuevoEmail != null && !actualUsuario.getId().equals(usuarioPorNuevoEmail.getId())) {
                throw new EmailExistException(EL_EMAIL_YA_SE_ENCUENTRA_REGISTRADO);
            }
            return actualUsuario;
        } else {
            if (usuarioPorNuevoNombreUsuario != null) {
                throw new UsernameExisteException(EL_USUARIO_YA_SE_ENCUENTRA_REGISTRADO);
            }

            if (usuarioPorNuevoEmail != null) {
                throw new EmailExistException(EL_EMAIL_YA_SE_ENCUENTRA_REGISTRADO);
            }

            return null;
        }
    }

    public List<UsuarioEntity> getUsuarios() {
        return usuarioRepositorio.findAll();
    }

    public UsuarioEntity buscarUsuarioPorNombreUsuario(String usuario) {
        return usuarioRepositorio.findByUsuario(usuario);
    }

    public UsuarioEntity buscarUsuarioPorEmail(String email) {
        return usuarioRepositorio.findByCorreoElectronico(email);
    }




    public UsuarioEntity login(UsuarioEntity usuarioDTO) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuarioDTO.getUsuario(), usuarioDTO.getClave()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        UsuarioEntity usuario = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuarioEntity();
        return usuario;
    }

    public HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtProvider.generateJwtToken(userPrincipal));
        return headers;
    }
}

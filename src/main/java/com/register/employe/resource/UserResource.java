package com.register.employe.resource;

import com.register.employe.domain.constant.SecurityConstant;
import com.register.employe.domain.exception.EmailExistException;
import com.register.employe.domain.exception.ExceptionHandling;
import com.register.employe.domain.exception.UserNotFoundException;
import com.register.employe.domain.exception.UsernameExisteException;
import com.register.employe.domain.service.UsuarioService;
import com.register.employe.infraestructure.model.UserPrincipal;
import com.register.employe.infraestructure.model.UsuarioEntity;
import com.register.employe.infraestructure.service.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/", "/user"})
@AllArgsConstructor
public class UserResource extends ExceptionHandling {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PostMapping(value = "/login")
    public ResponseEntity<UsuarioEntity> login(@RequestBody UsuarioEntity usuarioEntity) throws UserNotFoundException, UsernameExisteException, EmailExistException {
        authenticate(usuarioEntity.getUsuario(), usuarioEntity.getClave());
        UsuarioEntity usuarioLogin = usuarioService.buscarUsuarioPorNombreUsuario(usuarioEntity.getUsuario());
        UserPrincipal userPrincipal = new UserPrincipal(usuarioLogin);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(usuarioLogin, jwtHeader, HttpStatus.OK);
    }

//    @PostMapping(value = "/login")
//    public ResponseEntity<UsuarioEntity> login(@RequestBody UsuarioEntity usuarioDTO) {
//        UsuarioEntity login = usuarioService.login(usuarioDTO);
//        HttpHeaders jwtHeader = usuarioService.getJwtHeader((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        return new ResponseEntity<>(login, jwtHeader, HttpStatus.OK);
//    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String usuario, String clave) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario, clave));
    }

    @PostMapping(value = "")
    public ResponseEntity<UsuarioEntity> registrarUsuario(@RequestBody UsuarioEntity usuarioEntity) throws UserNotFoundException, UsernameExisteException, EmailExistException {
        UsuarioEntity nuevoUsuario = usuarioService.registrar(usuarioEntity.getNombres(), usuarioEntity.getApellidos(), usuarioEntity.getUsuario(), usuarioEntity.getCorreoElectronico());
        return ResponseEntity.ok(nuevoUsuario);
    }
}

package com.register.employe.domain.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.register.employe.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ExceptionHandling implements ErrorController {

    private static final String ACCOUNT_LOCKED = "Tu cuenta a sido bloqueada. Por favor contactar al administrador";
    private static final String METHOD_IS_NOT_ALLOWED = "Esta peticion del metodo no esta permitido para este endpoint. Por favor enviar un '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Ocurrio un error mientras procesaba la petición";
    private static final String INCORRECT_CREDENCIALES = "Usuario / clave incorrectas. Por favor intentelo de nuevo";
    private static final String ACCOUNT_DISABLED = "Tu cuenta a sido desabilitada. Si esto es un error, por favor contactar al administrador";
    private static final String ERROR_PROCESSING_FILE = "Ocurrio un error mientras procesaba el archivo";
    private static final String NOT_ENOUGH_PERMISSION = "No tienes suficientes permisos";
    private static final String ERROR_PATH = "/error";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> cuentaDeshabilitadaExceptcion() {
        return crearHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return crearHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENCIALES);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return crearHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return crearHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return crearHttpResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return crearHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameExisteException.class)
    public ResponseEntity<HttpResponse> usernameExisteException(UsernameExisteException exception) {
        return crearHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        return crearHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
        return crearHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException exception) {
//        return crearHttpResponse(HttpStatus.BAD_REQUEST, "Esta pagina no funciona");
//    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return crearHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return crearHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return crearHttpResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return crearHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    private ResponseEntity<HttpResponse> crearHttpResponse(HttpStatus httpStatus, String message) {
        HttpResponse httpResponse = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(httpResponse, httpStatus);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return crearHttpResponse(HttpStatus.NOT_FOUND, "No se encuentra mapeado para esta ruta");
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}

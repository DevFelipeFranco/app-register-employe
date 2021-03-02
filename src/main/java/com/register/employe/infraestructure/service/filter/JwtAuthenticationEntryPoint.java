package com.register.employe.infraestructure.service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.register.employe.domain.HttpResponse;
import static com.register.employe.domain.constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException {
        log.error("Ocurrio un error en el proceso de autenticación, por favor comunicarse con el administrador");
        HttpResponse httpResponse = new HttpResponse(FORBIDDEN.value(), FORBIDDEN, FORBIDDEN.getReasonPhrase().toUpperCase(), FORBIDDEN_MESSAGE);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}

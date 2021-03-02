package com.register.employe.infraestructure.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.register.employe.infraestructure.model.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.register.employe.domain.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

@Service
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(REGISTER_EMPLOYE_LCC)
                .withAudience(REGISTER_EMPLOYE_ADMINISTRATION)
                .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(Date.from(LocalDateTime.now().plusSeconds(EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String usuario, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                UsernamePasswordAuthenticationToken(usuario, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public Boolean isTokenValid(String usuario, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNoneEmpty(usuario) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;

        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(REGISTER_EMPLOYE_LCC).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }

        return verifier;
    }

    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}

package com.register.employe.infraestructure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private UsuarioEntity usuarioEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(this.usuarioEntity.getAutorizacion()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.usuarioEntity.getClave();
    }

    @Override
    public String getUsername() {
        return this.usuarioEntity.getUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.usuarioEntity.getSnBloqueado();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.usuarioEntity.getSnActivo();
    }
}

package com.register.employe.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.register.employe.domain.constant.Authority.*;

@Getter
@AllArgsConstructor
public enum Role {

    ROLE_USER(USER_AUTORITIES),
    ROLE_HR(HR_AUTORITIES),
    ROLE_MANAGER(MANAGER_AUTORITIES),
    ROLE_ADMIN(ADMIN_AUTORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTORITIES);

    private String[] authorities;
}

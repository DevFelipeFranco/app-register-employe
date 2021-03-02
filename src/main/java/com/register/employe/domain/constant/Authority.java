package com.register.employe.domain.constant;

public class Authority {

    public static final String[] USER_AUTORITIES = { "user:read" };
    public static final String[] HR_AUTORITIES = { "user:read", "user:update" };
    public static final String[] MANAGER_AUTORITIES = { "user:read", "user:update" };
    public static final String[] ADMIN_AUTORITIES = { "user:read", "user:create", "user:update" };
    public static final String[] SUPER_ADMIN_AUTORITIES = { "user:read", "user:create", "user:update", "user:delete" };
}

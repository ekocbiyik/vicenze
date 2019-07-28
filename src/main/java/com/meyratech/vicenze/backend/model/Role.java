package com.meyratech.vicenze.backend.model;

public class Role {

    public static final String ADMIN = "admin";
    public static final String ACCOUNTANT = "accountant";
    public static final String CUSTOMER = "customer";

    private Role() {
        // Static methods and fields only
    }

    public static String[] getAllRoles() {
        return new String[]{ADMIN, ACCOUNTANT};
    }

}
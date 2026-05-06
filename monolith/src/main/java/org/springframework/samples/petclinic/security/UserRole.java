package org.springframework.samples.petclinic.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enumeration of user roles in the Pet Clinic application.
 * Defines the three main roles: USER (pet owners), VET (veterinarians), and ADMIN (administrators).
 */
public enum UserRole implements GrantedAuthority {
    USER("ROLE_USER"),
    VET("ROLE_VET"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
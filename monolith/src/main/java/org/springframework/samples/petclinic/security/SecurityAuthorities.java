package org.springframework.samples.petclinic.security;

/**
 * Constants defining security authorities for different user roles in the Pet Clinic application.
 * Provides fine-grained permissions for users, veterinarians, and administrators.
 */
public final class SecurityAuthorities {

    // User authorities - pet owners can manage their own pets and visits
    public static final String USER_READ_PETS = "user:read:pets";
    public static final String USER_WRITE_PETS = "user:write:pets";
    public static final String USER_READ_VISITS = "user:read:visits";
    public static final String USER_WRITE_VISITS = "user:write:visits";

    // Veterinarian authorities - vets can view all pets/visits and manage visits
    public static final String VET_READ_ALL_PETS = "vet:read:all_pets";
    public static final String VET_READ_ALL_VISITS = "vet:read:all_visits";
    public static final String VET_WRITE_VISITS = "vet:write:visits";
    public static final String VET_READ_OWNERS = "vet:read:owners";

    // Administrator authorities - admins have full access
    public static final String ADMIN_READ_ALL = "admin:read:all";
    public static final String ADMIN_WRITE_ALL = "admin:write:all";
    public static final String ADMIN_DELETE_ALL = "admin:delete:all";
    public static final String ADMIN_MANAGE_USERS = "admin:manage:users";

    private SecurityAuthorities() {
        // Utility class - prevent instantiation
    }

    /**
     * Returns all authorities for USER role
     */
    public static String[] getUserAuthorities() {
        return new String[]{
            USER_READ_PETS,
            USER_WRITE_PETS,
            USER_READ_VISITS,
            USER_WRITE_VISITS
        };
    }

    /**
     * Returns all authorities for VET role
     */
    public static String[] getVetAuthorities() {
        return new String[]{
            VET_READ_ALL_PETS,
            VET_READ_ALL_VISITS,
            VET_WRITE_VISITS,
            VET_READ_OWNERS
        };
    }

    /**
     * Returns all authorities for ADMIN role
     */
    public static String[] getAdminAuthorities() {
        return new String[]{
            ADMIN_READ_ALL,
            ADMIN_WRITE_ALL,
            ADMIN_DELETE_ALL,
            ADMIN_MANAGE_USERS
        };
    }
}
# Pet Clinic Security Roles

This document describes the security roles defined in the SecurityConfig class and their access permissions.

## Roles

### ADMIN
- **Full access** to all application functionality
- Can access management features (`/management/**`)
- Can access veterinary functions (`/vets/**`)
- Can access clinic operations (`/owners/**`, `/pets/**`, `/visits/**`)

### VET
- **Veterinary access** to patient-related functionality
- Can access veterinary functions (`/vets/**`)
- Can access clinic operations (`/owners/**`, `/pets/**`, `/visits/**`)
- Cannot access management features

### STAFF
- **Basic clinic operations** access
- Can access clinic operations (`/owners/**`, `/pets/**`, `/visits/**`)
- Cannot access veterinary functions or management features

## Public Access

The following resources are accessible without authentication:
- Welcome page (`/`, `/welcome`)
- Static resources (`/resources/**`, `/webjars/**`, `/css/**`, `/js/**`, `/images/**`)

## Authentication

- Form-based login with custom login page (`/login`)
- BCrypt password encoding for secure password storage
- Successful login redirects to home page (`/`)
- Logout redirects to home page (`/`)

## Security Configuration

- CSRF protection is disabled for simplicity in this basic configuration
- All other requests require authentication
- Unauthorized access to protected resources redirects to login page
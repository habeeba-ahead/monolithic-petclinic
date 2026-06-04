# Basic Authentication Setup

This document describes the basic authentication implementation for the Pet Clinic application.

## Overview

Basic authentication has been implemented with in-memory user storage to establish the authentication foundation before implementing OAuth 2.0.

## User Roles and Credentials

The following users are configured:

| Username | Password | Role   | Access                                    |
|----------|----------|--------|-------------------------------------------|
| admin    | admin    | ADMIN  | Full access to all features              |
| vet      | vet      | VET    | Access to veterinarian features          |
| owner    | owner    | OWNER  | Access to owner features                 |

## Features

### Security Configuration
- **Location**: `src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java`
- **Features**:
  - In-memory user details service
  - BCrypt password encoding
  - Role-based access control
  - Form-based authentication

### Login Page
- **URL**: `/login`
- **Template**: `src/main/resources/templates/login.html`
- **Features**:
  - Bootstrap-styled login form
  - Error message display for invalid credentials
  - Success message display after logout
  - Demo user credentials displayed for convenience

### Access Control
- **Management pages**: Restricted to ADMIN role
- **Veterinarian pages**: Accessible to ADMIN and VET roles
- **Owner pages**: Accessible to ADMIN and OWNER roles
- **Static resources**: Publicly accessible

### Navigation
- Menu items are conditionally displayed based on user roles
- User dropdown shows current username and logout option
- Logout functionality redirects to login page with confirmation message

## Testing

Unit tests are provided to verify:
- Security configuration
- User role definitions
- Login controller functionality
- Password encoding
- Access control rules

## Next Steps

This basic authentication setup provides the foundation for implementing OAuth 2.0 authentication in future iterations.
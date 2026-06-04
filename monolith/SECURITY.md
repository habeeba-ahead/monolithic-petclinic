# Security Configuration

## Overview
This application now includes Spring Security to protect sensitive management endpoints.

## Security Requirements
- `/management/**` endpoints require ADMIN role
- Unauthorized access returns 403 Forbidden
- ADMIN users can access revenue data
- Non-admin users cannot access management endpoints

## Authentication
The application uses HTTP Basic Authentication with the following default users:

### Admin User
- Username: `admin`
- Password: `admin`
- Role: `ADMIN`
- Access: Can access all management endpoints

### Regular User
- Username: `user`
- Password: `password`
- Role: `USER`
- Access: Cannot access management endpoints

## Protected Endpoints
- `/management/revenue` - Financial revenue data (ADMIN only)
- `/management/**` - All management endpoints (ADMIN only)

## Public Endpoints
The following endpoints remain publicly accessible:
- `/` - Home page
- `/welcome` - Welcome page
- `/owners/**` - Owner management
- `/vets/**` - Veterinarian information
- `/pets/**` - Pet management
- `/visits/**` - Visit management
- `/api/**` - API endpoints
- Static resources (`/resources/**`, `/webjars/**`, `/css/**`, `/js/**`, `/images/**`)

## Testing
Security tests are included to verify:
- Management endpoints require authentication
- ADMIN role is required for management access
- Non-admin users receive 403 Forbidden
- Public endpoints remain accessible
- Invalid credentials are rejected

## Configuration
Security configuration is implemented in:
- `SecurityConfig.java` - Main security configuration
- `ManagementController.java` - Controller with `@PreAuthorize` annotations
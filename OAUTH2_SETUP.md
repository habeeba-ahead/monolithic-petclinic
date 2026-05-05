# OAuth2/OIDC Authentication with Google Cloud Identity

This document describes the OAuth2/OIDC authentication implementation for the Pet Clinic application.

## Overview

The application now uses OAuth2/OIDC authentication with Google Cloud Identity to provide secure user authentication. This replaces the previous absence of authentication with enterprise-grade security.

## Configuration

### Google Cloud Console Setup

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to "Credentials" and create OAuth 2.0 Client IDs
5. Configure the authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google` (for local development)
   - `https://your-domain.com/login/oauth2/code/google` (for production)

### Application Configuration

The OAuth2 configuration is defined in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID:your-google-client-id}
            client-secret: ${GOOGLE_CLIENT_SECRET:your-google-client-secret}
            scope:
              - openid
              - profile
              - email
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          google:
            issuer-uri: https://accounts.google.com
```

### Environment Variables

Set the following environment variables:

- `GOOGLE_CLIENT_ID`: Your Google OAuth2 client ID
- `GOOGLE_CLIENT_SECRET`: Your Google OAuth2 client secret

## Security Features

### Protected Resources

- All application endpoints except the welcome page and static resources require authentication
- Users are automatically redirected to Google authentication when accessing protected resources

### Authentication Flow

1. User accesses a protected resource
2. Application redirects to Google OAuth2 authorization endpoint
3. User authenticates with Google
4. Google redirects back to the application with authorization code
5. Application exchanges code for access token and creates authenticated session
6. User can now access protected resources

### User Interface

- Login/Logout buttons in the navigation bar
- User profile page showing authenticated user information
- Menu items are hidden for unauthenticated users

## Implementation Details

### Security Configuration

The `SecurityConfig` class configures:
- OAuth2 login with Google
- Public access to welcome page and static resources
- Authentication required for all other endpoints
- Logout functionality

### Controllers

- `AuthController`: Handles user profile display
- Existing controllers remain unchanged but are now protected by authentication

### Templates

- Updated navigation bar with authentication status
- User profile page for displaying authenticated user information
- Thymeleaf security integration for conditional rendering

## Testing

The implementation includes comprehensive tests:
- Security configuration tests
- OAuth2 integration tests
- Authentication flow tests
- Controller tests with mocked authentication

Run tests with:
```bash
./mvnw test
```

## Deployment Considerations

### Production Setup

1. Use HTTPS in production
2. Configure proper redirect URIs in Google Cloud Console
3. Use secure environment variable management for client secrets
4. Consider using Google Cloud Identity for enterprise features

### Monitoring

- Monitor authentication failures
- Track user login patterns
- Set up alerts for security events
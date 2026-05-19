# OAuth 2.0/OpenID Connect Setup Guide

## Overview
This application has been configured to use OAuth 2.0/OpenID Connect authentication with Google Cloud Identity Platform.

## Configuration

### Google Cloud Console Setup
1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to "Credentials" and create OAuth 2.0 Client IDs
5. Set authorized redirect URIs to: `http://localhost:8080/login/oauth2/code/google`

### Environment Variables
Set the following environment variables with your Google OAuth2 credentials:

```bash
export GOOGLE_CLIENT_ID=your-actual-google-client-id
export GOOGLE_CLIENT_SECRET=your-actual-google-client-secret
```

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

## Features Implemented

### Authentication Flow
- Unauthenticated users are redirected to Google OAuth2 login
- Successful authentication creates an authenticated user session
- Login/logout functionality in the navigation bar
- Protected resources require authentication

### Security Configuration
- Public access to home page and static resources
- OAuth2 login configuration with Google
- Automatic redirect to Google OAuth2 authorization endpoint
- Session management and logout functionality

### UI Integration
- Login/logout buttons in navigation
- User greeting when authenticated
- Conditional menu items based on authentication status
- Welcome page shows authentication status

## Testing
Run the tests to verify OAuth2 integration:

```bash
./mvnw test -Dtest=OAuth2ConfigurationTest
./mvnw test -Dtest=OAuth2LoginTest
./mvnw test -Dtest=SecurityIntegrationTest
```

## Running the Application
1. Set environment variables for Google OAuth2 credentials
2. Start the application: `./mvnw spring-boot:run`
3. Navigate to `http://localhost:8080`
4. Click "Login" to authenticate with Google
5. Access protected resources after authentication
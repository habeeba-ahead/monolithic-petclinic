# OAuth 2.0 Setup with Google Cloud Identity

This document describes how to configure OAuth 2.0 authentication with Google Cloud Identity for the Pet Clinic application.

## Prerequisites

1. Google Cloud Project with OAuth 2.0 credentials configured
2. OAuth 2.0 client ID and client secret from Google Cloud Console

## Configuration

### 1. Google Cloud Console Setup

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to "Credentials" in the API & Services section
5. Create OAuth 2.0 Client ID credentials
6. Configure authorized redirect URIs:
   - For local development: `http://localhost:8080/login/oauth2/code/google`
   - For production: `https://your-domain.com/login/oauth2/code/google`

### 2. Application Configuration

Set the following environment variables or update `application.properties`:

```bash
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
```

Or update `application.properties`:

```properties
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
```

## Features

- **OAuth 2.0 Flow**: Users are redirected to Google's OAuth consent screen
- **Authenticated Sessions**: Successful authentication creates a user session
- **Protected Resources**: Most application endpoints require authentication
- **Public Access**: Home page and veterinarians list remain publicly accessible
- **Logout**: Users can logout and clear their session

## Security Configuration

The application uses Spring Security with OAuth 2.0 client support:

- Protected endpoints: `/owners`, `/management`, etc.
- Public endpoints: `/`, `/vets`, static resources
- OAuth provider: Google Cloud Identity
- Scopes: `openid`, `profile`, `email`

## Testing

The application includes comprehensive tests for:

- OAuth 2.0 client configuration
- Authentication flow
- Protected resource access
- Public resource access

Run tests with:

```bash
./mvnw test
```

## Deployment

For GKE deployment, ensure:

1. OAuth credentials are stored as Kubernetes secrets
2. Redirect URIs are configured for the production domain
3. HTTPS is enabled for OAuth callback URLs
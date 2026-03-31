# OAuth 2.0/OIDC Setup with Google Cloud Identity

This application is configured to use OAuth 2.0/OIDC authentication with Google Cloud Identity.

## Configuration

### 1. Google Cloud Console Setup

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to "Credentials" and create OAuth 2.0 Client IDs
5. Configure the authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google` (for local development)
   - Add your production URLs as needed

### 2. Environment Variables

Set the following environment variables:

```bash
export GOOGLE_CLIENT_ID=your-actual-google-client-id
export GOOGLE_CLIENT_SECRET=your-actual-google-client-secret
```

### 3. Application Properties

The application is configured with the following OAuth2 settings in `application.properties`:

- Google OAuth2 client registration
- Proper scopes: `openid,profile,email`
- Google Cloud Identity provider endpoints

### 4. Security Features

- All endpoints require authentication except static resources and login page
- OAuth2 login with Google
- Secure logout functionality
- User information displayed in navigation bar

### 5. Testing

For testing purposes, you can use test values:
- `GOOGLE_CLIENT_ID=test-client-id`
- `GOOGLE_CLIENT_SECRET=test-client-secret`

The application will start but OAuth2 login will not work with test values.
package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
public class OAuth2ConfigurationTest {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void shouldConfigureGoogleOAuth2Client() {
        // Verify OAuth2 client configuration is loaded
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertNotNull(googleRegistration, "Google OAuth2 client registration should be configured");
        assertEquals("google", googleRegistration.getRegistrationId());
        assertEquals("test-client-id", googleRegistration.getClientId());
        assertEquals("test-client-secret", googleRegistration.getClientSecret());
    }

    @Test
    public void shouldConfigureCorrectScopes() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertTrue(googleRegistration.getScopes().contains("openid"), "Should include openid scope");
        assertTrue(googleRegistration.getScopes().contains("profile"), "Should include profile scope");
        assertTrue(googleRegistration.getScopes().contains("email"), "Should include email scope");
    }

    @Test
    public void shouldConfigureCorrectRedirectUri() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertEquals("{baseUrl}/login/oauth2/code/{registrationId}", 
                     googleRegistration.getRedirectUriTemplate(),
                     "Should have correct redirect URI template");
    }

    @Test
    public void shouldConfigureGoogleProvider() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertEquals("https://accounts.google.com/o/oauth2/v2/auth", 
                     googleRegistration.getProviderDetails().getAuthorizationUri(),
                     "Should have correct Google authorization URI");
        assertEquals("https://oauth2.googleapis.com/token", 
                     googleRegistration.getProviderDetails().getTokenUri(),
                     "Should have correct Google token URI");
    }
}
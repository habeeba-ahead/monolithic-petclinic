package org.springframework.samples.petclinic.config;

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
public class ApplicationConfigTest {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void shouldLoadGoogleOAuth2Configuration() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertNotNull(googleRegistration);
        assertEquals("google", googleRegistration.getRegistrationId());
        assertEquals("test-client-id", googleRegistration.getClientId());
        assertEquals("test-client-secret", googleRegistration.getClientSecret());
        assertTrue(googleRegistration.getScopes().contains("openid"));
        assertTrue(googleRegistration.getScopes().contains("profile"));
        assertTrue(googleRegistration.getScopes().contains("email"));
    }

    @Test
    public void shouldConfigureCorrectRedirectUri() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertEquals("{baseUrl}/login/oauth2/code/{registrationId}", 
                     googleRegistration.getRedirectUriTemplate());
    }
}
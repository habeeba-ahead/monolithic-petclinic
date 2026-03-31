package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify OAuth 2.0/OIDC acceptance criteria without MockMvc complexity
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
public class OAuth2SimpleAcceptanceTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Acceptance Criteria 1: spring-boot-starter-oauth2-client dependency added
     * Verify that OAuth2 client beans are available in the application context
     */
    @Test
    public void shouldHaveOAuth2ClientDependencyConfigured() {
        // Verify that OAuth2 client registration repository is available
        assertNotNull(clientRegistrationRepository, "OAuth2 client registration repository should be available");
        
        // Verify that the Google client registration exists
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        assertNotNull(googleRegistration, "Google OAuth2 client registration should be configured");
        
        // Verify OAuth2 client beans are in context
        assertTrue(applicationContext.containsBean("clientRegistrationRepository"), 
                   "ClientRegistrationRepository bean should be present");
        
        System.out.println("✓ OAuth2 client dependency is properly configured");
    }

    /**
     * Acceptance Criteria 2: OAuth 2.0 client configuration added to application.properties
     * Verify that Google OAuth2 client is properly configured
     */
    @Test
    public void shouldHaveOAuth2ClientConfigurationInProperties() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertNotNull(googleRegistration, "Google client registration should exist");
        assertEquals("google", googleRegistration.getRegistrationId());
        assertEquals("test-client-id", googleRegistration.getClientId());
        assertEquals("test-client-secret", googleRegistration.getClientSecret());
        
        // Verify scopes
        assertTrue(googleRegistration.getScopes().contains("openid"));
        assertTrue(googleRegistration.getScopes().contains("profile"));
        assertTrue(googleRegistration.getScopes().contains("email"));
        
        // Verify redirect URI template
        assertEquals("{baseUrl}/login/oauth2/code/{registrationId}", 
                     googleRegistration.getRedirectUriTemplate());
        
        System.out.println("✓ OAuth2 client configuration is properly set in application.properties");
    }

    /**
     * Acceptance Criteria 3: Google Cloud Identity configured as OAuth provider
     * Verify that Google provider endpoints are correctly configured
     */
    @Test
    public void shouldHaveGoogleCloudIdentityProviderConfigured() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertNotNull(googleRegistration, "Google client registration should exist");
        
        // Verify provider details
        assertEquals("https://accounts.google.com/o/oauth2/v2/auth", 
                     googleRegistration.getProviderDetails().getAuthorizationUri());
        assertEquals("https://oauth2.googleapis.com/token", 
                     googleRegistration.getProviderDetails().getTokenUri());
        assertEquals("https://openidconnect.googleapis.com/v1/userinfo", 
                     googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri());
        assertEquals("https://www.googleapis.com/oauth2/v3/certs", 
                     googleRegistration.getProviderDetails().getJwkSetUri());
        assertEquals("sub", 
                     googleRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
        
        System.out.println("✓ Google Cloud Identity provider is properly configured");
    }

    /**
     * Verify that security configuration beans are present
     */
    @Test
    public void shouldHaveSecurityConfigurationBeans() {
        // Check for security filter chain
        assertTrue(applicationContext.containsBean("springSecurityFilterChain"), 
                   "Spring Security filter chain should be present");
        
        // Check for OAuth2 authorization request resolver
        String[] beanNames = applicationContext.getBeanNamesForType(
            org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver.class);
        assertTrue(beanNames.length > 0, "OAuth2 authorization request resolver should be present");
        
        System.out.println("✓ Security configuration beans are properly configured");
    }
}
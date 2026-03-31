package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Final comprehensive test to verify all OAuth 2.0/OIDC acceptance criteria
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
public class OAuth2FinalAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Acceptance Criteria 1: spring-boot-starter-oauth2-client dependency added
     */
    @Test
    public void shouldHaveOAuth2ClientDependencyConfigured() {
        assertNotNull(clientRegistrationRepository, "OAuth2 client registration repository should be available");
        
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        assertNotNull(googleRegistration, "Google OAuth2 client registration should be configured");
        
        assertTrue(applicationContext.containsBean("clientRegistrationRepository"), 
                   "ClientRegistrationRepository bean should be present");
        
        System.out.println("✓ PASSED: spring-boot-starter-oauth2-client dependency added");
    }

    /**
     * Acceptance Criteria 2: OAuth 2.0 client configuration added to application.properties
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
        
        System.out.println("✓ PASSED: OAuth 2.0 client configuration added to application.properties");
    }

    /**
     * Acceptance Criteria 3: Google Cloud Identity configured as OAuth provider
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
        
        System.out.println("✓ PASSED: Google Cloud Identity configured as OAuth provider");
    }

    /**
     * Acceptance Criteria 4: Login/logout endpoints functional with Google authentication
     */
    @Test
    public void shouldHaveLoginPageWithGoogleAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("Sign in with Google")))
                .andExpect(content().string(containsString("/oauth2/authorization/google")));
        
        System.out.println("✓ PASSED: Login page displays Google authentication option");
    }

    @Test
    @WithMockUser
    public void shouldHaveWorkingLogoutEndpoint() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
        
        System.out.println("✓ PASSED: Logout endpoint is functional");
    }

    @Test
    @WithMockUser(username = "testuser@google.com")
    public void shouldAllowAccessToProtectedResourcesWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"));
        
        System.out.println("✓ PASSED: Authenticated users can access protected resources");
    }

    @Test
    public void shouldRedirectUnauthenticatedUsersToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        
        System.out.println("✓ PASSED: Unauthenticated users are redirected to login");
    }

    /**
     * Additional verification: Security configuration is properly set up
     */
    @Test
    public void shouldHaveSecurityConfigurationBeans() {
        assertTrue(applicationContext.containsBean("springSecurityFilterChain"), 
                   "Spring Security filter chain should be present");
        
        // Verify OAuth2 related beans exist
        assertTrue(applicationContext.containsBean("clientRegistrationRepository"), 
                   "ClientRegistrationRepository should be present");
        
        System.out.println("✓ PASSED: Security configuration beans are properly configured");
    }
}
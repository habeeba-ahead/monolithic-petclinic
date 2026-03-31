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
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test to verify all OAuth 2.0/OIDC acceptance criteria
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
public class OAuth2AcceptanceCriteriaTest {

    @Autowired
    private MockMvc mockMvc;

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
    }

    /**
     * Acceptance Criteria 4a: Login endpoint functional with Google authentication
     * Verify that login endpoint redirects to Google OAuth2 authorization
     */
    @Test
    public void shouldRedirectToGoogleOAuth2ForLogin() throws Exception {
        // Test that accessing protected resource redirects to OAuth2 login
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        
        String location = result.getResponse().getHeader("Location");
        assertTrue(location.contains("/oauth2/authorization/google"), 
                   "Should redirect to Google OAuth2 authorization endpoint");
    }

    /**
     * Acceptance Criteria 4b: Login page displays Google authentication option
     */
    @Test
    public void shouldDisplayGoogleLoginOption() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("Sign in with Google")))
                .andExpect(content().string(containsString("/oauth2/authorization/google")));
    }

    /**
     * Acceptance Criteria 4c: Authenticated users can access protected resources
     */
    @Test
    @WithMockUser(username = "testuser@google.com")
    public void shouldAllowAccessToProtectedResourcesWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"));
        
        MvcResult result = mockMvc.perform(get("/owners"))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        // Should either be OK (200) or redirect to find owners (302)
        assertTrue(status == 200 || status == 302, 
                   "Authenticated user should be able to access owners endpoint");
    }

    /**
     * Acceptance Criteria 4d: Logout endpoint functional
     */
    @Test
    @WithMockUser
    public void shouldProvideWorkingLogoutEndpoint() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    /**
     * Additional test: Verify logout success message is displayed
     */
    @Test
    public void shouldDisplayLogoutSuccessMessage() throws Exception {
        mockMvc.perform(get("/login?logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("You have been logged out successfully")));
    }

    /**
     * Additional test: Verify static resources are accessible without authentication
     */
    @Test
    public void shouldAllowAccessToStaticResourcesWithoutAuthentication() throws Exception {
        // These should not redirect to login
        MvcResult result1 = mockMvc.perform(get("/resources/css/petclinic.css"))
                .andReturn();
        int status1 = result1.getResponse().getStatus();
        assertNotEquals(302, status1, "Static resources should not redirect to login");
        
        MvcResult result2 = mockMvc.perform(get("/webjars/bootstrap/css/bootstrap.min.css"))
                .andReturn();
        int status2 = result2.getResponse().getStatus();
        assertNotEquals(302, status2, "Webjars should not redirect to login");
    }
}
package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test to verify all OAuth 2.0/OpenID Connect acceptance criteria
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "spring.security.oauth2.client.registration.google.scope=openid,profile,email",
    "spring.security.oauth2.client.provider.google.issuer-uri=https://accounts.google.com"
})
public class OAuth2AcceptanceCriteriaTest {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private WebApplicationContext context;

    /**
     * Acceptance Criteria 1: OAuth 2.0 client configuration added to application properties
     */
    @Test
    public void shouldHaveOAuth2ClientConfigurationInApplicationProperties() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration).isNotNull();
        assertThat(googleRegistration.getClientId()).isEqualTo("test-client-id");
        assertThat(googleRegistration.getClientSecret()).isEqualTo("test-client-secret");
        assertThat(googleRegistration.getScopes()).contains("openid", "profile", "email");
        assertThat(googleRegistration.getRedirectUriTemplate()).isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}");
    }

    /**
     * Acceptance Criteria 2: Google Cloud Identity Platform integration configured
     */
    @Test
    public void shouldHaveGoogleCloudIdentityPlatformIntegration() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration).isNotNull();
        assertThat(googleRegistration.getProviderDetails().getConfigurationMetadata().get("issuer"))
            .isEqualTo("https://accounts.google.com");
        assertThat(googleRegistration.getAuthorizationGrantType().getValue())
            .isEqualTo("authorization_code");
        assertThat(googleRegistration.getClientAuthenticationMethod().getValue())
            .isEqualTo("basic");
    }

    /**
     * Acceptance Criteria 3: Login redirects to OAuth provider
     */
    @Test
    public void shouldRedirectToOAuthProviderForLogin() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test that accessing protected resource redirects to OAuth login
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));

        // Test that accessing another protected resource also redirects
        mockMvc.perform(get("/vets"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    /**
     * Acceptance Criteria 4: Successful authentication creates authenticated user session
     */
    @Test
    @WithMockUser
    public void shouldCreateAuthenticatedUserSessionAfterSuccessfulAuthentication() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test that authenticated user can access protected resources
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk());
    }

    /**
     * Additional test: Verify public endpoints remain accessible without authentication
     */
    @Test
    public void shouldAllowAccessToPublicEndpointsWithoutAuthentication() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test public endpoints
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk());
    }
}
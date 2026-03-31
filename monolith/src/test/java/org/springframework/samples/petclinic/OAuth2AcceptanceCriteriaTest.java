package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class to verify all OAuth 2.0 acceptance criteria are met:
 * - OAuth 2.0 client configuration added to application.properties
 * - Google Cloud Identity configured as OAuth provider
 * - Login redirects to Google OAuth consent screen
 * - Successful authentication creates authenticated user session
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
class OAuth2AcceptanceCriteriaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void criteriaOne_OAuth2ClientConfigurationAddedToApplicationProperties() {
        // Verify OAuth 2.0 client configuration is properly loaded
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration).isNotNull();
        assertThat(googleRegistration.getClientId()).isEqualTo("test-client-id");
        assertThat(googleRegistration.getClientSecret()).isEqualTo("test-client-secret");
        assertThat(googleRegistration.getScopes()).contains("openid", "profile", "email");
    }

    @Test
    void criteriaTwo_GoogleCloudIdentityConfiguredAsOAuthProvider() {
        // Verify Google Cloud Identity is configured as the OAuth provider
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration.getProviderDetails().getAuthorizationUri())
            .isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
        assertThat(googleRegistration.getProviderDetails().getTokenUri())
            .isEqualTo("https://oauth2.googleapis.com/token");
        assertThat(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
            .isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
        assertThat(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName())
            .isEqualTo("sub");
    }

    @Test
    void criteriaThree_LoginRedirectsToGoogleOAuthConsentScreen() throws Exception {
        // Verify that accessing protected resources redirects to Google OAuth
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));

        // Verify that initiating OAuth flow redirects to Google consent screen
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", 
                    org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("accounts.google.com/o/oauth2/v2/auth"),
                        org.hamcrest.Matchers.containsString("client_id=test-client-id"),
                        org.hamcrest.Matchers.containsString("scope=openid%20profile%20email"),
                        org.hamcrest.Matchers.containsString("response_type=code")
                    )));
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = {"ROLE_USER"})
    void criteriaFour_SuccessfulAuthenticationCreatesAuthenticatedUserSession() throws Exception {
        // Verify that successful authentication allows access to protected resources
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());

        // Verify that authenticated user can access management features
        mockMvc.perform(get("/management/revenue"))
                .andExpect(status().isOk());
    }

    @Test
    void publicResourcesRemainAccessibleWithoutAuthentication() throws Exception {
        // Verify public resources are still accessible
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk());
    }
}
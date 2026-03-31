package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
class OAuth2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void shouldConfigureGoogleOAuth2Client() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration).isNotNull();
        assertThat(googleRegistration.getClientId()).isEqualTo("test-client-id");
        assertThat(googleRegistration.getClientSecret()).isEqualTo("test-client-secret");
        assertThat(googleRegistration.getScopes()).contains("openid", "profile", "email");
        assertThat(googleRegistration.getProviderDetails().getAuthorizationUri())
            .isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
        assertThat(googleRegistration.getProviderDetails().getTokenUri())
            .isEqualTo("https://oauth2.googleapis.com/token");
        assertThat(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
            .isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
    }

    @Test
    void shouldRedirectToGoogleAuthorizationWhenAccessingProtectedResource() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    void shouldAllowAccessToPublicResources() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldInitiateOAuth2AuthorizationFlow() throws Exception {
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
}
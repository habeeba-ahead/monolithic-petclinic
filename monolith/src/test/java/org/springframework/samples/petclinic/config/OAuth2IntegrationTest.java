package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "spring.security.oauth2.client.registration.google.scope=openid,profile,email",
    "spring.security.oauth2.client.provider.google.issuer-uri=https://accounts.google.com"
})
public class OAuth2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void shouldConfigureGoogleClientRegistration() {
        assertNotNull(clientRegistrationRepository.findByRegistrationId("google"));
    }

    @Test
    public void shouldRedirectToGoogleAuthorizationEndpoint() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://accounts.google.com/o/oauth2/v2/auth*"))
                .andExpect(redirectedUrlPattern("*client_id=test-client-id*"))
                .andExpect(redirectedUrlPattern("*scope=openid%20profile%20email*"));
    }

    @Test
    public void shouldHaveCorrectRedirectUri() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("*redirect_uri=http%3A//localhost/login/oauth2/code/google*"));
    }
}
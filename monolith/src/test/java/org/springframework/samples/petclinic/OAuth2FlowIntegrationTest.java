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

import static org.junit.jupiter.api.Assertions.*;
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
public class OAuth2FlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void shouldConfigureGoogleOAuth2Provider() {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertNotNull(registration);
        assertEquals("google", registration.getRegistrationId());
        assertEquals("test-client-id", registration.getClientId());
        assertEquals("test-client-secret", registration.getClientSecret());
        assertTrue(registration.getScopes().contains("openid"));
        assertTrue(registration.getScopes().contains("profile"));
        assertTrue(registration.getScopes().contains("email"));
    }

    @Test
    public void shouldRedirectUnauthenticatedUsersToGoogleLogin() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    @WithMockUser
    public void shouldAllowAuthenticatedUsersToAccessProtectedResources() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAllowPublicAccessToWelcomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"));
    }

    @Test
    public void shouldAllowPublicAccessToStaticResources() throws Exception {
        mockMvc.perform(get("/resources/css/petclinic.css"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void shouldDisplayUserProfileWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user"));
    }
}
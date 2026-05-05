package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
public class OAuth2LoginFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRedirectToGoogleOAuth2WhenAccessingProtectedResource() throws Exception {
        // When accessing a protected resource without authentication
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    public void shouldRedirectToGoogleAuthorizationEndpoint() throws Exception {
        // When initiating OAuth2 login flow
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://accounts.google.com/o/oauth2/v2/auth*"))
                .andExpect(redirectedUrlPattern("*client_id=test-client-id*"))
                .andExpect(redirectedUrlPattern("*scope=openid%20profile%20email*"));
    }

    @Test
    @WithMockUser
    public void shouldAllowAccessToProtectedResourceWhenAuthenticated() throws Exception {
        // When authenticated, should be able to access protected resources
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAllowAccessToPublicResources() throws Exception {
        // Public resources should be accessible without authentication
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"));
    }

    @Test
    @WithMockUser
    public void shouldDisplayUserProfileWhenAuthenticated() throws Exception {
        // User profile page should be accessible when authenticated
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user"));
    }
}
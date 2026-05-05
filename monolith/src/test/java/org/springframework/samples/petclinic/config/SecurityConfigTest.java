package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

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
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRedirectToLoginWhenAccessingProtectedResource() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    @WithMockUser
    public void shouldAllowAccessToProtectedResourceWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAllowAccessToPublicResources() throws Exception {
        mockMvc.perform(get("/resources/css/petclinic.css"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAllowAccessToWelcomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRedirectToGoogleOAuth2WhenAccessingLogin() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://accounts.google.com/o/oauth2/v2/auth*"));
    }
}
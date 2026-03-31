package org.springframework.samples.petclinic.config;

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
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "spring.security.oauth2.client.registration.google.scope=openid,profile,email",
    "spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}",
    "spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth",
    "spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token",
    "spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo",
    "spring.security.oauth2.client.provider.google.user-name-attribute=sub"
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToLoginWhenAccessingProtectedResource() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    void shouldAllowAccessToWelcomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldAllowAccessToProtectedResourceWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRedirectToGoogleOAuthWhenAccessingLogin() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", 
                    org.hamcrest.Matchers.containsString("accounts.google.com/o/oauth2/v2/auth")));
    }
}
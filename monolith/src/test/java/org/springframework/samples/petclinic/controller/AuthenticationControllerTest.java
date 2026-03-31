package org.springframework.samples.petclinic.controller;

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
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectUnauthenticatedUserToOAuth2Login() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/google"));
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = {"ROLE_USER"})
    void shouldAllowAuthenticatedUserToAccessProtectedResources() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = {"ROLE_USER"})
    void shouldCreateAuthenticatedUserSession() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("SPRING_SECURITY_CONTEXT", 
                    org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    void shouldAllowAccessToPublicPages() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk());
    }
}
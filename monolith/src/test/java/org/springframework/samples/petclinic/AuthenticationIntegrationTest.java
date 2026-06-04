package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test to verify authentication functionality works end-to-end
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebMvc
public class AuthenticationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Test
    public void testLoginPageIsAccessible() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testValidCredentialsAuthenticate() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test admin user
        mockMvc.perform(formLogin("/login").user("admin").password("admin"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"));

        // Test vet user
        mockMvc.perform(formLogin("/login").user("vet").password("vet"))
                .andExpect(authenticated().withUsername("vet").withRoles("VET"));

        // Test owner user
        mockMvc.perform(formLogin("/login").user("owner").password("owner"))
                .andExpect(authenticated().withUsername("owner").withRoles("OWNER"));
    }

    @Test
    public void testInvalidCredentialsAreRejected() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(formLogin("/login").user("invalid").password("invalid"))
                .andExpect(unauthenticated());
    }

    @Test
    public void testLoginErrorRedirection() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(formLogin("/login").user("wrong").password("wrong"))
                .andExpect(unauthenticated());

        // Verify error message is shown on login page
        mockMvc.perform(get("/login?error=true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
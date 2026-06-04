package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
public class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Test
    public void loginPageShouldBeAccessible() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/owners"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void shouldAuthenticateValidAdminUser() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(formLogin("/login").user("admin").password("admin"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"));
    }

    @Test
    public void shouldAuthenticateValidVetUser() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(formLogin("/login").user("vet").password("vet"))
                .andExpect(authenticated().withUsername("vet").withRoles("VET"));
    }

    @Test
    public void shouldAuthenticateValidOwnerUser() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(formLogin("/login").user("owner").password("owner"))
                .andExpect(authenticated().withUsername("owner").withRoles("OWNER"));
    }

    @Test
    public void shouldRejectInvalidCredentials() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(formLogin("/login").user("invalid").password("invalid"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminShouldAccessAllPages() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/management/revenue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VET")
    public void vetShouldAccessVetPages() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void ownerShouldAccessOwnerPages() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk());
    }
}
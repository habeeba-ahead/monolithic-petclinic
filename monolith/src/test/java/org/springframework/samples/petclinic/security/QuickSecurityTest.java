package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class QuickSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testManagementEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotAccessManagement() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanAccessManagement() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isOk());
    }

    @Test
    void testPublicEndpointAccessible() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }
}
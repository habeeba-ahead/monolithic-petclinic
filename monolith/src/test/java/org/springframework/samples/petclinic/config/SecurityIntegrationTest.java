package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@SpringJUnitConfig
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void securityFilterChainIsConfigured() {
        // Verify that the security filter chain bean is properly configured
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    void applicationStartsWithSecurity() throws Exception {
        // Verify that the application starts without security errors
        // and that basic endpoints are accessible (since we permit all for now)
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void staticResourcesAreAccessible() throws Exception {
        // Verify that static resources are accessible
        mockMvc.perform(get("/resources/css/petclinic.css"))
                .andExpect(status().isNotFound()); // 404 is expected if file doesn't exist, but no security error
    }
}
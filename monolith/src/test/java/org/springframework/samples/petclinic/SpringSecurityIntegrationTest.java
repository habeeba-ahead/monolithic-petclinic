package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class SpringSecurityIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        // Test that Spring Security is on the classpath and context loads successfully
        assertThat(applicationContext).isNotNull();
    }

    @Test
    public void shouldHaveSpringSecurityOnClasspath() throws Exception {
        // Test that Spring Security classes are available
        Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
        Class.forName("org.springframework.security.web.SecurityFilterChain");
    }

    @Test
    public void shouldRedirectToLoginPageWhenAccessingSecuredEndpoint() throws Exception {
        // Test that accessing any endpoint redirects to login page (Spring Security default behavior)
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void shouldShowLoginFormWhenAccessingLoginEndpoint() throws Exception {
        // Test that the default Spring Security login page is available
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Please sign in")));
    }
}
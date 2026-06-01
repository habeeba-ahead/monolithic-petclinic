package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test to verify that frontend functionality works correctly with updated dependencies
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class FrontendIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void testHomePageLoadsWithUpdatedDependencies() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void testOwnersPageLoadsWithUpdatedDependencies() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));
    }

    @Test
    public void testVeterinariansPageLoadsWithUpdatedDependencies() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(view().name("vets/vetList"));
    }

    @Test
    public void testStaticResourcesAreAccessible() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test that webjars resources are accessible
        mockMvc.perform(get("/webjars/jquery/3.7.1/jquery.min.js"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/webjars/bootstrap/5.3.2/css/bootstrap.min.css"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/webjars/jquery-ui/1.13.2/jquery-ui.min.js"))
                .andExpect(status().isOk());
    }
}
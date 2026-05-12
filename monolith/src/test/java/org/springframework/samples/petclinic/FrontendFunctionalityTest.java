package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test to verify that frontend functionality works correctly
 * with updated dependencies (jQuery 3.7.1, jQuery UI 1.13.2, Bootstrap 5.3.2)
 */
@SpringBootTest
@AutoConfigureWebMvc
public class FrontendFunctionalityTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    public void testWelcomePageLoadsWithFrontendDependencies() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("welcome"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("webjars/jquery/jquery.min.js")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("webjars/jquery-ui/jquery-ui.min.js")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("webjars/bootstrap/js/bootstrap.min.js")));
    }

    @Test
    public void testOwnersPageLoadsWithBootstrapStyling() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("navbar")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("container")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("form-group")));
    }

    @Test
    public void testVetsPageLoadsWithJQueryUIComponents() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(view().name("vets/vetList"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("webjars/jquery-ui/jquery-ui.min.js")));
    }

    @Test
    public void testJQueryDependencyIsAccessible() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test that jQuery webjar resource is accessible
        mockMvc.perform(get("/webjars/jquery/jquery.min.js"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/javascript"));
    }

    @Test
    public void testJQueryUIDependencyIsAccessible() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test that jQuery UI webjar resource is accessible
        mockMvc.perform(get("/webjars/jquery-ui/jquery-ui.min.js"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/javascript"));
    }

    @Test
    public void testBootstrapDependencyIsAccessible() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test that Bootstrap webjar resource is accessible
        mockMvc.perform(get("/webjars/bootstrap/js/bootstrap.min.js"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/javascript"));
    }

    @Test
    public void testBootstrapCSSIsAccessible() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test that Bootstrap CSS webjar resource is accessible
        mockMvc.perform(get("/webjars/bootstrap/css/bootstrap.min.css"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/css"));
    }

    @Test
    public void testNavigationMenuFunctionalityWithBootstrap() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test that navigation menu elements are present with Bootstrap classes
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("navbar-nav")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("navbar-toggle")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("glyphicon")));
    }
}
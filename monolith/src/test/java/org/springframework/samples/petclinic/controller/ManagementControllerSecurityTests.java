/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManagementControllerSecurityTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testManagementRevenueRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testManagementRevenueRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManagementRevenueAllowsAdminRole() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isOk());
    }

    @Test
    void testManagementEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/management/users"))
            .andExpect(status().isUnauthorized());
        
        mockMvc.perform(get("/management/reports"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testManagementEndpointsRequireAdminRole() throws Exception {
        mockMvc.perform(get("/management/users"))
            .andExpect(status().isForbidden());
        
        mockMvc.perform(get("/management/reports"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManagementEndpointsAllowAdminRole() throws Exception {
        // These endpoints don't exist yet, so they should return 404, not 403
        // This proves that authorization passed and we reached the controller layer
        mockMvc.perform(get("/management/users"))
            .andExpect(status().isNotFound());
        
        mockMvc.perform(get("/management/reports"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testPublicEndpointsAreAccessible() throws Exception {
        // Test that public endpoints are still accessible without authentication
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
        
        mockMvc.perform(get("/welcome"))
            .andExpect(status().isOk());
    }
}
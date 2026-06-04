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
package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Acceptance tests for management endpoint security requirements:
 * - /management/** endpoints require ADMIN role
 * - Unauthorized access returns 403 Forbidden
 * - ADMIN users can access revenue data
 * - Non-admin users cannot access management endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
class ManagementSecurityAcceptanceTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void managementEndpointsRequireAdminRole() throws Exception {
        // Test that /management/** endpoints require ADMIN role
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthorizedAccessReturnsForbidden() throws Exception {
        // Test that non-admin users get 403 Forbidden
        mockMvc.perform(get("/management/revenue")
                .with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminUsersCanAccessRevenueData() throws Exception {
        // Test that ADMIN users can access revenue data
        mockMvc.perform(get("/management/revenue")
                .with(httpBasic("admin", "admin")))
            .andExpect(status().isOk());
    }

    @Test
    void nonAdminUsersCannotAccessManagementEndpoints() throws Exception {
        // Test that non-admin users cannot access management endpoints
        mockMvc.perform(get("/management/revenue")
                .with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
        
        // Test other potential management endpoints
        mockMvc.perform(get("/management/users")
                .with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
        
        mockMvc.perform(get("/management/reports")
                .with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
    }

    @Test
    void publicEndpointsRemainAccessible() throws Exception {
        // Verify that public endpoints are still accessible without authentication
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
        
        mockMvc.perform(get("/welcome"))
            .andExpect(status().isOk());
        
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk());
    }

    @Test
    void invalidCredentialsAreRejected() throws Exception {
        // Test that invalid credentials are properly rejected
        mockMvc.perform(get("/management/revenue")
                .with(httpBasic("admin", "wrongpassword")))
            .andExpect(status().isUnauthorized());
        
        mockMvc.perform(get("/management/revenue")
                .with(httpBasic("invaliduser", "password")))
            .andExpect(status().isUnauthorized());
    }
}
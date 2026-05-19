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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.config.SecurityConfig;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OwnerController.class)
@Import(SecurityConfig.class)
class OwnerControllerSecurityTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ClinicService service;

    @Test
    void testOwnerEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void testOwnerEndpointAllowedForReceptionist() throws Exception {
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VETERINARIAN")
    void testOwnerEndpointAllowedForVeterinarian() throws Exception {
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOwnerEndpointAllowedForAdmin() throws Exception {
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void testOwnerSearchAllowedForReceptionist() throws Exception {
        // Mock service to return multiple owners to avoid redirect behavior
        Owner owner1 = new Owner();
        owner1.setFirstName("John");
        owner1.setLastName("Doe");
        
        Owner owner2 = new Owner();
        owner2.setFirstName("Jane");
        owner2.setLastName("Doe");
        
        given(this.service.ownerByLastName("")).willReturn(asList(owner1, owner2));
        
        mockMvc.perform(get("/owners"))
            .andExpect(status().isOk());
    }
}
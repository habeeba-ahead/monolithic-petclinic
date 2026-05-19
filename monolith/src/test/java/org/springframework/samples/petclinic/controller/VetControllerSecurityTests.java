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
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VetController.class)
@Import(SecurityConfig.class)
class VetControllerSecurityTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ClinicService service;

    @Test
    void testVetEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/vets"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void testVetEndpointAllowedForReceptionist() throws Exception {
        given(this.service.allVets()).willReturn(asList(new Vet()));
        
        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VETERINARIAN")
    void testVetEndpointAllowedForVeterinarian() throws Exception {
        given(this.service.allVets()).willReturn(asList(new Vet()));
        
        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testVetEndpointAllowedForAdmin() throws Exception {
        given(this.service.allVets()).willReturn(asList(new Vet()));
        
        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk());
    }
}
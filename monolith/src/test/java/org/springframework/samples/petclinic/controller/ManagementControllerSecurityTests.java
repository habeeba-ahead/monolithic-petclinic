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
import org.springframework.samples.petclinic.model.YearlyRevenue;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ManagementController.class)
@Import(SecurityConfig.class)
class ManagementControllerSecurityTests {

    static final List<YearlyRevenue> EXPECTED_REVENUES = asList(
        new YearlyRevenue(2020, 333L)
    );

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ClinicService service;

    @Test
    void testManagementEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void testManagementEndpointForbiddenForReceptionist() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VETERINARIAN")
    void testManagementEndpointForbiddenForVeterinarian() throws Exception {
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManagementEndpointAllowedForAdmin() throws Exception {
        given(this.service.listYearlyRevenue()).willReturn(EXPECTED_REVENUES);
        
        mockMvc.perform(get("/management/revenue"))
            .andExpect(status().isOk());
    }
}
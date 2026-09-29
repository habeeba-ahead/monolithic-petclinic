package org.springframework.samples.petclinic.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.dto.VetResponse;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(VetRestController.class)
class VetRestControllerTest {
    public static final Integer ID=1;
    public static final String FirstName="James";
    public static final String LastName="Carter";


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicService;

    @Test
    @WithMockUser
    void shouldReturnAllVets() throws Exception{
        VetResponse vetResponse=new VetResponse();
        vetResponse.setId(ID);
        vetResponse.setFirstName(FirstName);
        vetResponse.setLastName(LastName);
        vetResponse.setSpecialties(Arrays.asList());
        when(clinicService.getAllVets()).thenReturn(Arrays.asList(vetResponse));

        mockMvc.perform(get("/api/v1/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("James"))
                .andExpect(jsonPath("$[0].lastName").value("Carter"))
                .andExpect(jsonPath("$[0].specialties").isArray());
    }


    }

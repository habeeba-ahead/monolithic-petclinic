package org.springframework.samples.petclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.dto.VetResponse;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vets")
public class VetRestController {

    private final ClinicService clinicService;
    @Autowired
    public VetRestController(ClinicService clinicService){
        this.clinicService=clinicService;
    }

    @GetMapping
    public List<VetResponse> getAllVets(){
        return clinicService.getAllVets();
    }
}

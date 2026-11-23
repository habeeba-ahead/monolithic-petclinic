package org.springframework.samples.petclinic.controller;

import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ManagementController {

    private final ClinicService clinicService;

    public ManagementController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping("/management/revenue")
    public String showRevenue(Map<String, Object> model) {
        model.put("revenues", clinicService.listYearlyRevenue());
        return "management/showRevenue";
    }

}

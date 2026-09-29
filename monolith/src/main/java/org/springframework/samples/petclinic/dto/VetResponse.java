package org.springframework.samples.petclinic.dto;


import org.springframework.samples.petclinic.model.Specialty;

import java.util.List;

public class VetResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private List<Specialty> specialties;

    public Integer getId() {
        return id;
    }

    public List<Specialty> getSpecialties() {
        return specialties;
    }

        public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    //setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSpecialties(List<Specialty> specialties){
        this.specialties=specialties;
    }

}

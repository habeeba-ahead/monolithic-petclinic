package org.springframework.samples.petclinic.mapper;


import org.springframework.samples.petclinic.dto.VetResponse;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

    public VetResponse toResponse(Vet vet){
        VetResponse vetResponse=new VetResponse();
        vetResponse.setId(vet.getId());
        vetResponse.setFirstName(vet.getFirstName());
        vetResponse.setLastName(vet.getLastName());
        vetResponse.setSpecialties(vet.getSpecialties());
        return vetResponse;
    }
}

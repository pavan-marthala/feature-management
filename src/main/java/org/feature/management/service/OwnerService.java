package org.feature.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feature.management.entity.Owner;
import org.feature.management.exception.ResourceNotFoundException;
import org.feature.management.models.OwnerModel;
import org.feature.management.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    private final ObjectMapper mapper;

    public Owner addNewOwner(OwnerModel ownerModel){
        log.info("creating new owner");
        Owner owner = mapper.convertValue(ownerModel, Owner.class);
        owner.setFeatures(new HashSet<>());
        ownerRepository.save(owner);
        log.info("owner created successfully");
        return owner;
    }

    public Owner findOwnerById(Long id){
        log.info("finding owner by id: {}", id);
        return ownerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
    }

    public void deleteOwner(Long id){
        log.info("deleting owner with id: {}", id);
        if(ownerRepository.existsById(id)){
            ownerRepository.deleteById(id);
        }
        log.info("owner deleted successfully");
    }

    public Owner updateOwner(OwnerModel ownerModel,Long id){
        log.info("updating owner with id: {}", ownerModel.getEmail());
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

        return null;
    }

    public Owner findOwnerByEmail(String email){
        log.info("finding owner by email: {}", email);
        return ownerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + email));
    }
    public List<Owner> findAllOwners(){
        log.info("finding all owners");
        return ownerRepository.findAll();
    }

}

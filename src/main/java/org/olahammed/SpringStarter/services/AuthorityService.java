package org.olahammed.SpringStarter.services;

import java.util.Optional;

import org.olahammed.SpringStarter.models.Authority;
import org.olahammed.SpringStarter.repositories.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {
    @Autowired
    private AuthorityRepository authorityRepository;

    public Optional<Authority> getById(Long id){
        return authorityRepository.findById(id);
    }

    public Authority save(Authority authority) {
        // if(authority.getId() == null) {
        //     authority.setCreatedAt(LocalDateTime.now());
        // }
        return authorityRepository.save(authority);
    }
}
package com.cesar.Authentication.service;

import com.cesar.Authentication.persistence.entity.AuthUserEntity;
import com.cesar.Authentication.persistence.repository.AuthUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {

    private final AuthUserRepository userRepo;

    public AuthUserService(AuthUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public AuthUserEntity loadByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
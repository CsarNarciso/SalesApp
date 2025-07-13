package com.cesar.Authentication.util;

import com.cesar.Authentication.persistence.entity.AuthUserEntity;
import com.cesar.Authentication.persistence.entity.RoleEntity;
import com.cesar.Authentication.persistence.repository.AuthUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class AuthUserUtils {

    private final AuthorityUtils authorityUtils;
    private final AuthUserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthUserUtils(AuthorityUtils authorityUtils, AuthUserRepository userRepo) {
        this.authorityUtils = authorityUtils;
        this.userRepo = userRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public void initTestUser() {
        // Only use persisted role from roles map
        RoleEntity userRole = authorityUtils.getUserRole();
        userRepo.save(buildUser("user", "password", Set.of(userRole)));
    }

    @Transactional
    public void initDefaultAdmins() {
        RoleEntity adminRole = authorityUtils.getAdminRole();
        userRepo.save(buildUser("admin", "letmein", Set.of(adminRole)));
    }

    public AuthUserEntity buildUser(String username, String password, Set<RoleEntity> roles) {
        return AuthUserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .build();
    }
}
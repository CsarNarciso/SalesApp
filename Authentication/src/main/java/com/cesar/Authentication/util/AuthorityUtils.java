package com.cesar.Authentication.util;

import com.cesar.Authentication.persistence.entity.PermissionEntity;
import com.cesar.Authentication.persistence.entity.PermissionEnum;
import com.cesar.Authentication.persistence.entity.RoleEntity;
import com.cesar.Authentication.persistence.entity.RoleEnum;
import com.cesar.Authentication.persistence.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.cesar.Authentication.persistence.entity.PermissionEnum.*;
import static com.cesar.Authentication.persistence.entity.RoleEnum.*;

@Component
public class AuthorityUtils {

    private final RoleRepository roleRepo;
    private RoleEntity userRole;
    private RoleEntity adminRole;

    public AuthorityUtils(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Transactional
    public void initDefaultPermissionsAndRoles() {
        // Build permissions
        PermissionEntity read = buildPermission(READ);
        PermissionEntity write = buildPermission(WRITE);
        PermissionEntity delete = buildPermission(DELETE);
        PermissionEntity refactor = buildPermission(REFACTOR);

        // Build roles (no saving yet)
        RoleEntity userRole = buildRole(USER, Set.of(read, write));
        RoleEntity adminRole = buildRole(ADMIN, Set.of(read, write, delete, refactor));

        // Save roles
        roleRepo.saveAll(List.of(userRole, adminRole));
    }

    public RoleEntity getAdminRole(){
        if(adminRole == null){
            adminRole = roleRepo.findByName(ADMIN)
                    .orElseThrow(() -> new RuntimeException("Default ADMIN role not found"));
        }
        return adminRole;
    }

    public RoleEntity getUserRole(){
        if(userRole == null){
            userRole = roleRepo.findByName(USER)
                    .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        }
        return userRole;
    }

    public PermissionEntity buildPermission(PermissionEnum name) {
        return PermissionEntity.builder().name(name).build();
    }

    public RoleEntity buildRole(RoleEnum name, Set<PermissionEntity> permissions) {
        return RoleEntity.builder().name(name).permissions(permissions).build();
    }
}
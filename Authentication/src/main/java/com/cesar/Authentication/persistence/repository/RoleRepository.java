package com.cesar.Authentication.persistence.repository;

import java.util.Optional;

import com.cesar.Authentication.persistence.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cesar.Authentication.persistence.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>{

	Optional<RoleEntity> findByName(RoleEnum name);
}
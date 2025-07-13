package com.cesar.Authentication.persistence.repository;

import java.util.Optional;

import com.cesar.Authentication.persistence.entity.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long>{
	Optional<AuthUserEntity> findByUsername(String username);
}
package com.cesar.User.service;

import com.cesar.User.persistence.entity.UserEntity;
import com.cesar.User.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

	public UserEntity create(UserEntity user) {
        return userRepo.save(user);
    }

    public UserEntity findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }
	
	public List<UserEntity> findAll() {
        return userRepo.findAll();
    }
}
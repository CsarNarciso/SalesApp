package com.cesar.Authentication.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
}
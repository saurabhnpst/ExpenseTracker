package com.saurabh.ExpenseTracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    // Nullable for Google users
    private String password;

    @Column(unique = true)
    private String email;

    private String provider;

    private String providerId;

    @Column(nullable = false)
    private String role = "USER";

    public User() {
    }

    // Local registration
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.provider = "LOCAL";
    }

    // Google registration
    public User(
            String username,
            String email,
            String provider,
            String providerId,
            String role
    ) {
        this.username = username;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }
}
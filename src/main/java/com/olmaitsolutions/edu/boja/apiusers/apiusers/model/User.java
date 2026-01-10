package com.olmaitsolutions.edu.boja.apiusers.apiusers.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "active_status")
    private Boolean activeStatus = false;

    @Column(name = "user_role", length = 20)
    private String userRole;

    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @Column(name = "last_login_date")
    private OffsetDateTime lastLoginDate;

    @Column(name = "disable_date")
    private OffsetDateTime disableDate;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
}

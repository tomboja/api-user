package com.olmaitsolutions.edu.boja.apiusers.apiusers.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private Boolean emailVerified;
    private String email;
    private Boolean activeStatus;
    private String userRole;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastLoginDate;
    private OffsetDateTime disableDate;
    private String phoneNumber;
    private String profilePictureUrl;
    private Integer failedLoginAttempts;
}

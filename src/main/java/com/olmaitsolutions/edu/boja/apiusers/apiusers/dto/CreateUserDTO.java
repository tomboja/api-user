package com.olmaitsolutions.edu.boja.apiusers.apiusers.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between {min} and {max} characters long")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between {min} and {max} characters long")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Size(min = 8, max = 100, message = "Email must be between {min} and {max} characters long")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 8, max = 50, message = "Username must be between {min} and {max} characters long")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters long")
    private String password;

    @Size(max = 20, message = "Phone number must not exceed {max} characters")
    private String phoneNumber;

    // Optional: allow caller to specify role and active flag
    @Size(max = 20, message = "User role must not exceed {max} characters")
    private String userRole;

    private Boolean activeStatus;
}

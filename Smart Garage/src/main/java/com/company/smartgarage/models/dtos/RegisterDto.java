package com.company.smartgarage.models.dtos;

import com.company.smartgarage.helpers.annotations.ValidEmail;
import com.company.smartgarage.helpers.annotations.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterDto {
    @NotBlank(message = "First name cannot be empty")
    @Size(min = 6, max = 38, message = "Names length must be between 6 and 38 symbols")
    private String names;
    @NotBlank(message = "Email cannot be empty")
    @ValidEmail
    private String email;
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 symbols")
    private String username;
    @NotBlank
    @Size(min = 10, max = 10, message = "Phone number must be 10 symbols long")
    private String phoneNumber;
    @NotBlank(message = "Password cannot be empty")
    @ValidPassword
    private String password;
    @NotBlank(message = "Password confirmation can't be empty")
    private String passwordConfirm;
}

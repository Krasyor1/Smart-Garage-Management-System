package com.company.smartgarage.models.dtos;

import com.company.smartgarage.helpers.annotations.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 30, message = "Username should be between 5 and 30 characters long")
    private String username;
    @NotBlank(message = "Password cannot be empty")
    @ValidPassword
    private String password;
}

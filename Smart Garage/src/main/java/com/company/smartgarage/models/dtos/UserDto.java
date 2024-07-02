package com.company.smartgarage.models.dtos;

import com.company.smartgarage.helpers.annotations.ValidEmail;
import com.company.smartgarage.helpers.annotations.ValidPassword;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserDto {

    @NotNull
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 symbols")
    private String username;
    @NotNull
    @ValidEmail
    @Size(min = 10, max = 60, message = "e-mail should be between 10 and 60 characters long")
    private String email;
    @ValidPassword
    @NotNull(message = "Password cannot be empty")
    private String password;
    @NotNull
    @Size(min = 6, max = 38, message = "Names length must be between 6 and 38 symbols")
    private String names;
    @NotNull
    @Size(min = 10, max = 10, message = "Phone number must be 10 symbols long")
    private String phoneNumber;
    @NotNull
    private UserRole userRole;
    @NotNull
    private UserStatus userStatus;
}

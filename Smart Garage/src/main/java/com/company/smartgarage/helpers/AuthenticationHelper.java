package com.company.smartgarage.helpers;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.enums.UserStatus;
import com.company.smartgarage.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    public static final String AUTHORIZATION = "Authorization";
    public static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String INACTIVE = "This user is inactive";
    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if(!headers.containsKey(AUTHORIZATION)) {
            throw new AuthorizationException("The request resource requires authentication.");
        }

        try {
            String username = headers.getFirst(AUTHORIZATION);

            return userService.getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException("Invalid username.");
        }
    }

    public User tryGetCurrentUser(HttpSession session) {
        String currentUsername = (String) session.getAttribute("loggedUser");

        if (currentUsername == null) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userService.getByUsername(currentUsername);
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = userService.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            if (user.getUserStatus().equals(UserStatus.INACTIVE)) {
                throw new AuthorizationException(INACTIVE);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }
}

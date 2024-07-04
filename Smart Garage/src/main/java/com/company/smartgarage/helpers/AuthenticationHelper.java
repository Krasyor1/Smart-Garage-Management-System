package com.company.smartgarage.helpers;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.enums.UserStatus;
import com.company.smartgarage.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Enumeration;

@Component
public class AuthenticationHelper {

    public static final String AUTHORIZATION = "Authorization";
    public static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String INACTIVE = "This user is inactive";
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder();
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
        // Log all session attributes for debugging
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            System.out.println("Session Attribute: " + attributeName + " = " + session.getAttribute(attributeName));
        }

        String currentUsername = (String) session.getAttribute("loggedUser");

        if (currentUsername == null) {
            System.out.println("No user is currently logged in.");
            throw new AuthorizationException("Invalid authentication.");
        }

        try {
            return userService.getByUsername(currentUsername);
        } catch (EntityNotFoundException e) {
            System.out.println("User not found: " + currentUsername);
            throw new AuthorizationException("Invalid authentication.");
        }
    }

    public User verifyAuthentication(String username, String rawPassword) {
        try {
            User user = userService.getByUsername(username);

            // Check if the stored password is already encrypted
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user;
            }

            // If the password is not encrypted, compare it as plain text
            if (user.getPassword().equals(rawPassword)) {
                // Encrypt the plain text password and update the user record
                String encryptedPassword = passwordEncoder.encode(rawPassword);
                user.setPassword(encryptedPassword);
                return user;
            }

            throw new AuthorizationException("Invalid username or password.");

        } catch (EntityNotFoundException e) {
            throw new AuthorizationException("Invalid username or password.");
        }
    }
}

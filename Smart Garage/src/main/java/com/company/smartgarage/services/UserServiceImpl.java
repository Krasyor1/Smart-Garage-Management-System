package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.*;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.enums.UserStatus;
import com.company.smartgarage.models.filters.CustomerFilterOptions;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;
import com.company.smartgarage.repositories.contracts.UserRepository;
import com.company.smartgarage.services.contracts.UserService;
import com.company.smartgarage.services.contracts.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    public static final String ONLY_AUTHORIZED_USERS_HAS_PERMISSION =
            "Only administrators or employees can create,delete or update users";
    public static final String ONLY_REGISTERED_CUSTOMER_OR_AUTHORIZED_HAS_ACCESS =
            "Only registered and active customers has access";
    private final UserRepository userRepository;
    private final VehicleService vehicleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, VehicleService vehicleService) {
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
    }

    @Override
    public List<User> get(EmployeeFilterOptions filterOptions, User authorizedUser) {
        hasEmployeeAccess(authorizedUser);
        return userRepository.get(filterOptions);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getById(int id) {
        return userRepository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public User getByPhone(String phone) {
        return userRepository.getByPhone(phone);
    }


    @Override
    public User create(User userToCreate) {

        boolean duplicateExists = true;

        try {
            userRepository.getByUsername(userToCreate.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("User", "username", userToCreate.getUsername());
        }

        duplicateExists = true;

        try {
            userRepository.getByEmail(userToCreate.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("User", "email", userToCreate.getEmail());
        }

        duplicateExists = true;

        try {
            userRepository.getByPhone(userToCreate.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("User", "phone number", userToCreate.getPhoneNumber());
        }

        userRepository.create(userToCreate);
        return userToCreate;
    }

    @Override
    public User update(User userToUpdate, User authorizedUser) {
        if (!hasEmployeeAccess(authorizedUser)) {
            throw new AuthorizationException(ONLY_AUTHORIZED_USERS_HAS_PERMISSION);
        }

        User existingUser = userRepository.getByUsername(userToUpdate.getUsername());
        if (existingUser != null && !(existingUser.getUserId() == userToUpdate.getUserId())) {
            throw new EntityDuplicateException("User", "username", userToUpdate.getUsername());
        }
        existingUser = userRepository.getByEmail(userToUpdate.getEmail());
        if (existingUser != null && !(existingUser.getUserId() == userToUpdate.getUserId())) {
            throw new EntityDuplicateException("User", "email", userToUpdate.getEmail());
        }
        existingUser = userRepository.getByPhone(userToUpdate.getPhoneNumber());
        if (existingUser != null && !(existingUser.getUserId() == userToUpdate.getUserId())) {
            throw new EntityDuplicateException("User", "phone number", userToUpdate.getPhoneNumber());
        }

        userRepository.update(userToUpdate);
        return userToUpdate;
    }

    @Override
    public void closeAccount(int id, User authorizedUser) {
        if (!hasEmployeeAccess(authorizedUser)) {
            throw new AuthorizationException(ONLY_AUTHORIZED_USERS_HAS_PERMISSION);
        }

        boolean userExist = true;
        try {
            userRepository.getById(id);
        } catch (EntityNotFoundException e) {
            userExist = false;
        }

        if (!userExist) {
            throw new EntityNotFoundException("User", id);
        }

        userRepository.closeAccount(id);
    }

    private boolean hasCustomerAccess(User customer) {
        return customer.getUserRole().equals(UserRole.CUSTOMER);
    }

    @Override
    public Set<Visit> getCustomerVisits(CustomerFilterOptions filterOptions, User customer) {
        if (!hasCustomerAccess(customer) && !hasEmployeeAccess(customer)) {
            throw new AuthorizationException(ONLY_REGISTERED_CUSTOMER_OR_AUTHORIZED_HAS_ACCESS);
        }

        try {
            return userRepository.getCustomerVisits(filterOptions, customer);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    @Override
    public String getVisitReport(User customer, Vehicle vehicle, String visitDate) {
        if (!hasCustomerAccess(customer, vehicle) && !hasEmployeeAccess(customer)) {
            throw new AuthorizationException(ONLY_REGISTERED_CUSTOMER_OR_AUTHORIZED_HAS_ACCESS);
        }
        return userRepository.getVisitReport(vehicle, customer, visitDate);
    }

    private boolean hasEmployeeAccess(User authorizedUser) {
        return (authorizedUser.getUserRole().equals(UserRole.ADMINISTRATOR)
                || authorizedUser.getUserRole().equals(UserRole.EMPLOYEE));
    }

    private boolean hasCustomerAccess(User customer, Vehicle vehicle) {
        return customer.getUserRole().equals(UserRole.CUSTOMER)
                && customer.getUserStatus().equals(UserStatus.ACTIVE)
                && customer.getVehicles().contains(vehicle);
    }

    @Override
    public void updateResetToken(String token, String email) {
        User user = userRepository.getByEmail(email);
        Token newToken = createToken(token);
        user.setToken(newToken);
        userRepository.update(user);
    }

    @Override
    public Token createToken(String token) {
        Token newToken = new Token();
        newToken.setToken(token);
        newToken.setTokenCreationDate(LocalDateTime.now());
        return userRepository.createToken(newToken);
    }

    @Override
    public Token findToken(String token) {
       return userRepository.findToken(token);
    }

    @Override
    public User findByResetToken(String token) {
        User user = userRepository.getByResetToken(token);

        LocalDateTime tokenCreationDate = user.getToken().getTokenCreationDate();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(tokenCreationDate, now);

        if (duration.toHours() <= 1) {
            return user;
        } else {
            user.getToken().setTokenCreationDate(null);
            throw new AuthorizationException("Reset token has expired");
        }
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.getToken().setToken(null);
        user.getToken().setTokenCreationDate(null);
        userRepository.update(user);
    }


}

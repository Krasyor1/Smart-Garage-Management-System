package com.company.smartgarage.controllers.rest;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.UserMapper;
import com.company.smartgarage.models.*;
import com.company.smartgarage.models.dtos.UserDto;
import com.company.smartgarage.models.filters.CustomerFilterOptions;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;
import com.company.smartgarage.services.contracts.UserService;
import com.company.smartgarage.services.contracts.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/garage-api/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;
    private final VehicleService vehicleService;

    public UserRestController(UserService userService,
                              UserMapper userMapper,
                              VehicleService vehicleService,
                              AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
        this.vehicleService = vehicleService;
    }


    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Conflict")
    @Operation(summary = "Get all users",
            description = "Retrieve a list of all users with option to filter and sort")
    @GetMapping
    public List<User> get(
            @RequestHeader HttpHeaders headers,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String names,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String vehicleModel,
            @RequestParam(required = false) String vehicleBrand,
            @RequestParam(required = false) String visitCount,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {

        try {
            User authorizedUser = authenticationHelper.tryGetUser(headers);
            EmployeeFilterOptions filterOptions = new EmployeeFilterOptions(
                    username,
                    email,
                    names,
                    phoneNumber,
                    vehicleModel,
                    vehicleBrand,
                    visitCount,
                    sortBy,
                    sortOrder);
            return userService.get(filterOptions, authorizedUser);
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException a) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, a.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Find user by ID")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        try {
            return userService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Find user by username")
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        try {
            return userService.getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Find user by email")
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        try {
            return userService.getByEmail(email);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Find user by phone number")
    @GetMapping("/phone/{phoneNumber}")
    public User getUserByPhone(@PathVariable String phoneNumber) {
        try {
            return userService.getByPhone(phoneNumber);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @Operation(summary = "Create user")
    @PostMapping
    public User createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User userToCreate = userMapper.fromDto(userDto);
            userService.create(userToCreate);
            return userToCreate;
        } catch (EntityDuplicateException d) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, d.getMessage());
        } catch (AuthorizationException a) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, a.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    public User update(@RequestHeader HttpHeaders headers,
                       @PathVariable int id,
                       @Valid @RequestBody UserDto userDto) {
        try {
            User authorizedUser = authenticationHelper.tryGetUser(headers);
            User userToUpdate = userMapper.fromDto(id, userDto);
            userService.update(userToUpdate, authorizedUser);
            return userToUpdate;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException d) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, d.getMessage());
        } catch (AuthorizationException a) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, a.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Change user's status to Inactive")
    @DeleteMapping("/{id}")
    public void closeAccount(@RequestHeader HttpHeaders headers,
                       @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.closeAccount(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get customer visits with options to filter them")
    @GetMapping("/customer/vehicle-visit")
    public Set<Visit> getCustomerVisits(@RequestHeader HttpHeaders headers,
                                        @RequestParam(required = false) String dateTime,
                                        @RequestParam(required = false) String licensePlate) {

        try {
            User customer = authenticationHelper.tryGetUser(headers);
            CustomerFilterOptions customerFilterOptions = new CustomerFilterOptions(licensePlate, dateTime);
            return userService.getCustomerVisits(customerFilterOptions, customer);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get customer visit report")
    @GetMapping("/customer/visit-report/{licensePlate}")
    public String getVisitReport(@RequestHeader HttpHeaders headers,
                                 @PathVariable String licensePlate,
                                 @RequestParam String visitDate) {

        try {
            User customer = authenticationHelper.tryGetUser(headers);
            Vehicle vehicle = vehicleService.getByLicensePlate(licensePlate);
            return userService.getVisitReport(customer, vehicle, visitDate);
        } catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch(AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }




}

package com.company.smartgarage.models.filters;

import lombok.Data;

import java.util.Optional;

@Data
public class EmployeeFilterOptions {
    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> names;
    private Optional<String> phoneNumber;
    private Optional<String> vehicleModel;
    private Optional<String> vehicleBrand;
    private Optional<String> visitBetween;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;


    public EmployeeFilterOptions() {
        this(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public EmployeeFilterOptions(String username,
                                 String email,
                                 String names,
                                 String phoneNumber,
                                 String vehicleModel,
                                 String vehicleBrand,
                                 String visitBetween,
                                 String sortBy,
                                 String sortOrder) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.names = Optional.ofNullable(names);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
        this.vehicleModel = Optional.ofNullable(vehicleModel);
        this.vehicleBrand = Optional.ofNullable(vehicleBrand);
        this.visitBetween = Optional.ofNullable(visitBetween);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }
}
package com.company.smartgarage.models.enums;

public enum UserRole {
    ADMINISTRATOR,
    EMPLOYEE,
    CUSTOMER;

    @Override
    public String toString() {
        return switch (this) {
            case ADMINISTRATOR -> "Administrator";
            case EMPLOYEE -> "Employee";
            case CUSTOMER -> "Customer";
        };
    }
}

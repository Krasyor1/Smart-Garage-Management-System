package com.company.smartgarage.models.dtos;

import lombok.Data;
@Data
public class FilterEmployeeDto {
    private String username;
    private String email;
    private String names;
    private String phoneNumber;
    private String vehicleModel;
    private String vehicleBrand;
    private String visitBetween;
    private String sortBy;
    private String sortOrder;
}

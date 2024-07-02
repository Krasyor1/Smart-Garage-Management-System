package com.company.smartgarage.models.dtos;

import lombok.Data;

@Data
public class FilterVehiclesDto {

    private String modelName;
    private String brandName;
    private String licensePlate;
    private String vin;
    private Integer minYear;
    private Integer maxYear;
    private String sortBy;
    private String orderBy;
}

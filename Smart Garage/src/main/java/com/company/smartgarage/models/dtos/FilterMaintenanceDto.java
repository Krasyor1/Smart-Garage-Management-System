package com.company.smartgarage.models.dtos;

import lombok.Data;

@Data
public class FilterMaintenanceDto {
    private String searchByName;
    private double searchByPrice;
    private String serviceName;
    private double minPrice;
    private double maxPrice;
    private String sort;
    private String order;
}

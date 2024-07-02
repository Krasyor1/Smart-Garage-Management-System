package com.company.smartgarage.models.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.time.Year;
import java.util.Optional;

@Data
public class VehicleFilterOptions {

    private Optional<String> modelName;
    private Optional<String> brandName;
    private Optional<String> licensePlate;
    private Optional<String> vin;
    private Optional<Integer> minYear;
    private Optional<Integer> maxYear;
    private Optional<String> sortBy;
    private Optional<String> orderBy;

    public VehicleFilterOptions(){
        this(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public VehicleFilterOptions(String modelName,
                                String brandName,
                                String licensePlate,
                                String vin,
                                Integer minYear,
                                Integer maxYear,
                                String sortBy,
                                String orderBy) {
        this.modelName = Optional.ofNullable(modelName);
        this.brandName = Optional.ofNullable(brandName);
        this.licensePlate = Optional.ofNullable(licensePlate);
        this.vin = Optional.ofNullable(vin);
        this.minYear = Optional.ofNullable(minYear);
        this.maxYear = Optional.ofNullable(maxYear);
        this.sortBy = Optional.ofNullable(sortBy);
        this.orderBy = Optional.ofNullable(orderBy);
    }
}

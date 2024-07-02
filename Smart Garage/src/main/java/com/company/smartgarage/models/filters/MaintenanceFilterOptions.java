package com.company.smartgarage.models.filters;

import lombok.Data;

import java.util.Optional;

@Data
public class MaintenanceFilterOptions {
    private Optional<String> searchByName;
    private Optional<Double> searchByPrice;
    private Optional<String> serviceName;
    private Optional<Double> minPrice;
    private Optional<Double> maxPrice;
    private Optional<String> sortBy;
    private Optional<String> orderBy;

    public MaintenanceFilterOptions() {
        this(
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public MaintenanceFilterOptions(String searchByName,
                                    Double searchByPrice,
                                    String serviceName,
                                    Double minPrice,
                                    Double maxPrice,
                                    String sortBy,
                                    String orderBy) {
        this.searchByName = Optional.ofNullable(searchByName);
        this.searchByPrice = Optional.ofNullable(searchByPrice);
        this.serviceName = Optional.ofNullable(serviceName);
        this.minPrice = Optional.ofNullable(minPrice);
        this.maxPrice = Optional.ofNullable(maxPrice);
        this.sortBy = Optional.ofNullable(sortBy);
        this.orderBy = Optional.ofNullable(orderBy);
    }
}

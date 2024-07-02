package com.company.smartgarage.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaintenanceDto {
    @Size(min = 2, max = 32, message = "Service name must be between 2 and 32 symbols")
    @NotNull
    private String serviceName;
    @Positive(message = "Price cannot be negative")
    @NotNull
    private double price;
    @Positive(message = "Please select category")
    @NotNull
    private int categoryId;
}

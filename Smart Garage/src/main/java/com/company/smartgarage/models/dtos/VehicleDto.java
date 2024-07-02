package com.company.smartgarage.models.dtos;

import com.company.smartgarage.helpers.annotations.ValidLicencePlate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleDto {

    @NotNull(message = "Licence Plate cannot be empty.")
    @ValidLicencePlate
    private String licensePlate;
    @NotNull(message = "VIN cannot be empty.")
    @Size(min = 17, max = 17, message = "VIN must be 17 symbols.")
    private String vin;
    @NotNull(message = "Model ID cannot be empty.")
    private int modelId;
    @NotNull(message = "Creation year cannot be empty.")
    @Min(value = 1886, message = "Year of production must be between 1886 and 2023")
    private int creationYear;
    @NotNull(message = "Owner ID cannot be empty.")
    private int ownerId;
}

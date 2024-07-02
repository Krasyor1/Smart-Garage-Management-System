package com.company.smartgarage.models.dtos;

import com.company.smartgarage.helpers.annotations.ValidLicencePlate;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.enums.Currency;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class VisitDto {

    @NotNull(message = "There must be a vehicle licensePlate")
    @ValidLicencePlate
    private String vehicleLicensePlate;
    @NotEmpty(message = "Please select at least one service")
    private Set<Integer> services;
    @NotNull(message = "Please choose currency")
    private Currency currencyDto;
}

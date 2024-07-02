package com.company.smartgarage.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModelDto {

    @NotNull
    @Size(min = 2, max = 125, message = "Model name length must be between 2 and 125 symbols")
    private String modelName;
    @NotNull
    @Positive
    private int brandId;
}

package com.company.smartgarage.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaintenanceCategoryDto {
    @Size(min = 2, max = 32, message = "Category name must be between 2 and 32 symbols")
    @NotNull
    private String categoryName;
}

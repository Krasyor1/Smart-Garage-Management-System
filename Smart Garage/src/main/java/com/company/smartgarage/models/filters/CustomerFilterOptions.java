package com.company.smartgarage.models.filters;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class CustomerFilterOptions  {

    private Optional<String> licencePlate;
    private Optional<String> localDate;


    public CustomerFilterOptions() {
        this(
                null,
                null);
    }

    public CustomerFilterOptions(String licencePlate,
                                 String localDate) {
        this.licencePlate = Optional.ofNullable(licencePlate);
        this.localDate = Optional.ofNullable(localDate);

    }
}

package com.company.smartgarage.models.filters;

import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.VisitStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
public class VisitFilterOptions {
    private Optional<Double> minPrice;
    private Optional<Double> maxPrice;
    private Optional<Currency> currency;
    private Optional<VisitStatus> status;
    private Optional<LocalDate> startDate;
    private Optional<LocalDate> endDate;
    private Optional<Integer> page;

    public VisitFilterOptions() {
        this(
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public VisitFilterOptions(Double minPrice,
                              Double maxPrice,
                              Currency currency,
                              VisitStatus status,
                              LocalDate startDate,
                              LocalDate endDate,
                              Integer page) {
        this.minPrice = Optional.ofNullable(minPrice);
        this.maxPrice = Optional.ofNullable(maxPrice);
        this.currency = Optional.ofNullable(currency);
        this.status = Optional.ofNullable(status);
        this.startDate = Optional.ofNullable(startDate);
        this.endDate = Optional.ofNullable(endDate);
        this.page = Optional.ofNullable(page);
    }

}

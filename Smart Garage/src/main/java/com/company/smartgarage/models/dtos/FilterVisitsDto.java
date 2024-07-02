package com.company.smartgarage.models.dtos;

import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.VisitStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterVisitsDto {
    private double minPrice;
    private double maxPrice;
    private Currency currency;
    private VisitStatus visitStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private int page;
}

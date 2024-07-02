package com.company.smartgarage.helpers;

import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.dtos.MaintenanceDto;
import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.services.contracts.MaintenanceCategoryService;
import com.company.smartgarage.services.contracts.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceMapper {
    private final MaintenanceService service;
    private final MaintenanceCategoryService categoryService;
    @Autowired
    public MaintenanceMapper(MaintenanceService service, MaintenanceCategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    public Maintenance toObject(MaintenanceDto dto) {
        Maintenance maintenance = new Maintenance();
        return toObject(maintenance, dto);
    }

    public Maintenance toObject(int id, MaintenanceDto dto) {
        Maintenance maintenance = service.getById(id);
        return toObject(maintenance, dto);
    }

    public MaintenanceDto toDto(Maintenance maintenance) {
        MaintenanceDto dto = new MaintenanceDto();
        dto.setServiceName(maintenance.getServiceName());
        dto.setPrice(maintenance.getPrice());
        dto.setCategoryId(maintenance.getCategory().getId());
        return dto;
    }

    private Maintenance toObject(Maintenance maintenance, MaintenanceDto dto) {
        maintenance.setServiceName(dto.getServiceName());
        maintenance.setPrice(dto.getPrice());
        maintenance.setCurrency(Currency.BGN);
        maintenance.setCategory(categoryService.getById(dto.getCategoryId()));
        maintenance.setAvailable(true);
        return maintenance;
    }
}

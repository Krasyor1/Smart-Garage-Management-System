package com.company.smartgarage.services.contracts;

import com.company.smartgarage.models.MaintenanceCategory;

import java.util.List;

public interface MaintenanceCategoryService {
    List<MaintenanceCategory> getAll();

    MaintenanceCategory getById(int id);

    void create(MaintenanceCategory category);

    void update(MaintenanceCategory category);
}

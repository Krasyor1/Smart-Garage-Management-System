package com.company.smartgarage.services;

import com.company.smartgarage.models.MaintenanceCategory;
import com.company.smartgarage.repositories.contracts.MaintenanceCategoryRepository;
import com.company.smartgarage.services.contracts.MaintenanceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceCategoryServiceImpl implements MaintenanceCategoryService {
    private final MaintenanceCategoryRepository maintenanceCategoryRepository;

    @Autowired
    public MaintenanceCategoryServiceImpl(MaintenanceCategoryRepository maintenanceCategoryRepository) {
        this.maintenanceCategoryRepository = maintenanceCategoryRepository;
    }

    @Override
    public List<MaintenanceCategory> getAll() {
        return maintenanceCategoryRepository.getAll();
    }

    @Override
    public MaintenanceCategory getById(int id) {
        return maintenanceCategoryRepository.getById(id);
    }

    @Override
    public void create(MaintenanceCategory category) {
        maintenanceCategoryRepository.create(category);
    }

    @Override
    public void update(MaintenanceCategory category) {
        maintenanceCategoryRepository.update(category);
    }


}

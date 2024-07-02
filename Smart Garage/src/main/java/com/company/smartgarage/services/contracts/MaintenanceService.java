package com.company.smartgarage.services.contracts;

import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;

import java.util.List;

public interface MaintenanceService {
    List<Maintenance> getAll();

    List<Maintenance> get(MaintenanceFilterOptions filterOptions);

    Maintenance getByServiceName(String serviceName);
    Maintenance getById(int id);

    void create(Maintenance maintenance, User user);

    void update(Maintenance maintenance, User user);

    void delete(int id, User user);
}

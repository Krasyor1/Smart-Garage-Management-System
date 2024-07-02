package com.company.smartgarage.repositories.contracts;

import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;

import java.util.List;

public interface MaintenanceRepository extends BaseCrudRepository<Maintenance> {
    List<Maintenance> get(MaintenanceFilterOptions filterOptions);
    Maintenance getByServiceName(String serviceName, boolean condition);
}

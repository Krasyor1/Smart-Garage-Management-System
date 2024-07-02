package com.company.smartgarage.repositories.contracts;

import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.filters.VehicleFilterOptions;

import java.util.List;

public interface VehicleRepository extends BaseCrudRepository<Vehicle> {

    List<Vehicle> get(VehicleFilterOptions filterOptions);
    Vehicle getByLicensePlate(String license);
    Vehicle getByVin(String vin);
    List<Vehicle> getByPhoneNumber(String phone);
    List<Visit> getVisits(int id);

}

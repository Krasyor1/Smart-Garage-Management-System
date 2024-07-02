package com.company.smartgarage.services.contracts;

import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.filters.VehicleFilterOptions;

import java.util.List;

public interface VehicleService {

    List<Vehicle> get(VehicleFilterOptions filterOptions);

    Vehicle getById(int id);

    Vehicle create(Vehicle vehicle, User user);

    Vehicle update(Vehicle vehicleToUpdate, User user);

    void delete(int id, User user);

    List<Vehicle> getAll();

    Vehicle getByLicensePlate (String license);

    Vehicle getByVin(String vin);
    List<Vehicle> getByPhoneNumber(String phone);
    List<Visit> getVisits(int id, User user);
}

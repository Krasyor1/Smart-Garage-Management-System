package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.filters.VehicleFilterOptions;
import com.company.smartgarage.repositories.contracts.VehicleRepository;
import com.company.smartgarage.services.contracts.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VehicleServiceImpl implements VehicleService {

    public static final String AUTHORIZATION_ERROR = "Only administrators or employees can modify vehicles";
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository)
    {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Vehicle> get(VehicleFilterOptions filterOptions) {
        return vehicleRepository.get(filterOptions);
    }

    @Override
    public Vehicle getById(int id) {

        return vehicleRepository.getById(id);
    }

    @Override
    public Vehicle create(Vehicle vehicleToCreate, User user) {

        if(!hasPermission(user)){
            throw new AuthorizationException(AUTHORIZATION_ERROR);
        }

        boolean duplicateExists = true;

        try{
            vehicleRepository.getByVin(vehicleToCreate.getVin());
        }catch (EntityNotFoundException e){
            duplicateExists = false;
        }

        if(duplicateExists){
            throw new EntityDuplicateException("Vehicle", "VIN", vehicleToCreate.getVin());
        }

        duplicateExists = true;

        try {
            vehicleRepository.getByLicensePlate(vehicleToCreate.getLicensePlate());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("Vehicle", "license plate", vehicleToCreate.getLicensePlate());
        }

        vehicleRepository.create(vehicleToCreate);
        return vehicleToCreate;
    }

    @Override
    public Vehicle update(Vehicle vehicleToUpdate, User user) {
        if(!hasPermission(user)){
            throw new AuthorizationException(AUTHORIZATION_ERROR);
        }

        boolean duplicateExists = true;

        Vehicle existingVehicle;
        try{
            existingVehicle = vehicleRepository.getByVin(vehicleToUpdate.getVin());
            if(existingVehicle.getId() == vehicleToUpdate.getId()){
                duplicateExists = false;
            }
        }catch (EntityNotFoundException e){
            duplicateExists = false;
        }

        if(duplicateExists){
            throw new EntityDuplicateException("Vehicle", "VIN", vehicleToUpdate.getVin());
        }

        duplicateExists = true;

        try {
            existingVehicle = vehicleRepository.getByLicensePlate(vehicleToUpdate.getLicensePlate());
            if(existingVehicle.getId() == vehicleToUpdate.getId()){
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("Vehicle", "license plate", vehicleToUpdate.getLicensePlate());
        }

        vehicleRepository.update(vehicleToUpdate);
        return vehicleToUpdate;
    }

    @Override
    public void delete(int id, User user) {
        if(!hasPermission(user)){
            throw new AuthorizationException(AUTHORIZATION_ERROR);
        }

        vehicleRepository.delete(id);
    }

    @Override
    public List<Vehicle> getAll(){
        return vehicleRepository.getAll(); }

    @Override
    public Vehicle getByLicensePlate(String license) {

        return vehicleRepository.getByLicensePlate(license);
    }

    @Override
    public Vehicle getByVin(String vin) {

        return vehicleRepository.getByVin(vin);
    }

   @Override
   public List<Vehicle> getByPhoneNumber(String phone) {

      return vehicleRepository.getByPhoneNumber(phone);
   }

    @Override
    public List<Visit> getVisits(int id, User user) {
        return vehicleRepository.getVisits(id);
    }


    private boolean hasPermission(User authorizedUser) {
        return (authorizedUser.getUserRole().equals(UserRole.ADMINISTRATOR)
                || authorizedUser.getUserRole().equals(UserRole.EMPLOYEE));
    }
}

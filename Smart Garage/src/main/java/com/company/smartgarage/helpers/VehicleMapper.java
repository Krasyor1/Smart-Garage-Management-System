package com.company.smartgarage.helpers;

import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.dtos.VehicleDto;
import com.company.smartgarage.services.contracts.ModelService;
import com.company.smartgarage.services.contracts.UserService;
import com.company.smartgarage.services.contracts.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class VehicleMapper {

    private final VehicleService vehicleService;
    private final UserService userService;
    private final ModelService modelService;

    @Autowired
    public VehicleMapper(VehicleService vehicleService, UserService userService, ModelService modelService){
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.modelService = modelService;
    }

    public Vehicle fromDto(VehicleDto vehicleDto){
        Vehicle vehicle = new Vehicle();
        return fromDto(vehicle, vehicleDto);
    }

    public Vehicle fromDto(int id, VehicleDto vehicleDto){
        Vehicle vehicle = vehicleService.getById(id);
//        vehicle.setId(id);
        vehicle.setModel(modelService.getById(vehicleDto.getModelId()));
        vehicle.setVin(vehicleDto.getVin().toUpperCase());
        vehicle.setLicensePlate(vehicleDto.getLicensePlate().toUpperCase());
        vehicle.setYear(vehicleDto.getCreationYear());
        vehicle.setOwner(userService.getById(vehicleDto.getOwnerId()));
        return vehicle;
    }

    public Vehicle fromDto(Vehicle vehicle, VehicleDto vehicleDto){

        vehicle.setModel(modelService.getById(vehicleDto.getModelId()));
        vehicle.setVin(vehicleDto.getVin().toUpperCase());
        vehicle.setLicensePlate(vehicleDto.getLicensePlate().toUpperCase());
        vehicle.setYear(vehicleDto.getCreationYear());
        vehicle.setOwner(userService.getById(vehicleDto.getOwnerId()));

        return vehicle;
    }

    public VehicleDto toDto(Vehicle vehicle){
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setLicensePlate(vehicle.getLicensePlate());
        vehicleDto.setVin(vehicle.getVin());
        vehicleDto.setModelId(vehicle.getModel().getId());
        vehicleDto.setCreationYear(vehicle.getYear());
        vehicleDto.setOwnerId(vehicle.getOwner().getUserId());

        return vehicleDto;
    }
}

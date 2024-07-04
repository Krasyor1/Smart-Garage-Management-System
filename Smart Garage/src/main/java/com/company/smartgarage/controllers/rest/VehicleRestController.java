package com.company.smartgarage.controllers.rest;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.VehicleMapper;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.dtos.VehicleDto;
import com.company.smartgarage.models.filters.VehicleFilterOptions;
import com.company.smartgarage.services.contracts.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/garage-api/vehicles")
public class VehicleRestController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final AuthenticationHelper authenticationHelper;


    public VehicleRestController(VehicleService vehicleService, VehicleMapper vehicleMapper, AuthenticationHelper authenticationHelper) {
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Conflict")
    @Operation(summary = "Get all vehicles", description = "Retrieve a list of all vehicles with option to filter and sort.")
    @GetMapping
    public List<Vehicle> get(@RequestParam(required = false) String modelName,
                             @RequestParam(required = false) String brandName,
                             @RequestParam(required = false) String licensePlate,
                             @RequestParam(required = false) String vin,
                             @RequestParam(required = false) Integer minYear,
                             @RequestParam(required = false) Integer maxYear,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false) String sortOrder){
        VehicleFilterOptions filterOptions = new VehicleFilterOptions(
                modelName,
                brandName,
                licensePlate,
                vin,
                minYear,
                maxYear,
                sortBy,
                sortOrder);

        return vehicleService.get(filterOptions);
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get vehicle by ID")
    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable int id){
        try{
            return vehicleService.getById(id);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get vehicle by VIN")
    @GetMapping("/vin/{vin}")
    public Vehicle getVehicleByVin(@PathVariable String vin){
        try {
            return vehicleService.getByVin(vin);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get vehicle by its owner's phone number")
    @GetMapping("/phone/{phone}")
   public List<Vehicle> getVehicleByPhoneNumber(@PathVariable String phone){
       try {
           return vehicleService.getByPhoneNumber(phone);
       }catch (EntityNotFoundException e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       }
   }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @Operation(summary = "Create vehicle")
   @PostMapping
    public void create(@RequestHeader HttpHeaders headers,
                       @Valid @RequestBody VehicleDto dto){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Vehicle vehicle = vehicleMapper.fromDto(dto);
            vehicleService.create(vehicle, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Update vehicle")
    @PutMapping("/{id}")
    public void update(@RequestHeader HttpHeaders headers,
                       @PathVariable int id,
                       @Valid @RequestBody VehicleDto dto){
        try {
            User baseUser = authenticationHelper.tryGetUser(headers);
            Vehicle vehicle = vehicleMapper.fromDto(id, dto);
            vehicleService.update(vehicle, baseUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Delete vehicle")
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers,
                       @PathVariable int id){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            vehicleService.delete(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}


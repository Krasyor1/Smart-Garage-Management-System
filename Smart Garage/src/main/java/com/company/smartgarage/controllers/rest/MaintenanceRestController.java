package com.company.smartgarage.controllers.rest;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.MaintenanceMapper;
import com.company.smartgarage.helpers.RateHelper;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.dtos.CurrencyDto;
import com.company.smartgarage.models.dtos.MaintenanceDto;
import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.services.contracts.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.company.smartgarage.helpers.RateHelper.calculatePrice;
import static com.company.smartgarage.helpers.RateHelper.getRate;

@RestController
@RequestMapping("/api/services")
public class MaintenanceRestController {
    public static final String FROM_CURRENCY = "BGN";
    private final MaintenanceService maintenanceService;
    private final MaintenanceMapper maintenanceMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public MaintenanceRestController(MaintenanceService maintenanceService,
                                     MaintenanceMapper maintenanceMapper, AuthenticationHelper authenticationHelper, RateHelper rateHelper) {
        this.maintenanceService = maintenanceService;
        this.maintenanceMapper = maintenanceMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @Operation(summary = "Get all services",
            description = "Retrieve a list of all services with option to filter and sort")
    @GetMapping
    public List<Maintenance> get(
            @RequestParam(required = false) String searchByName,
            @RequestParam(required = false) Double searchByPrice,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String orderBy
    ) {
        MaintenanceFilterOptions maintenanceFilterOptions = new MaintenanceFilterOptions(
                searchByName, searchByPrice, null, minPrice, maxPrice, sortBy, orderBy
        );
        return maintenanceService.get(maintenanceFilterOptions);
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get service by name")
    @GetMapping("/service-name")
    public Maintenance getByName(@RequestParam String name, @RequestBody CurrencyDto currencyDto) {
        try {
            String currency = currencyDto.getCurrency().toString();

            Maintenance maintenance = maintenanceService.getByServiceName(name);

            double rate = getRate(currency, FROM_CURRENCY);

            double converted = calculatePrice(FROM_CURRENCY, maintenance.getPrice(), rate);

            maintenance.setPrice(converted);
            maintenance.setCurrency(Currency.valueOf(currency));
            return maintenance;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get service by ID")
    @GetMapping("/{id}")
    public Maintenance getById(@PathVariable int id, @RequestBody CurrencyDto currencyDto) {
        try {
            String currency = currencyDto.getCurrency().toString();

            Maintenance maintenance = maintenanceService.getById(id);

            double rate = getRate(currency, FROM_CURRENCY);

            double converted = calculatePrice(FROM_CURRENCY, maintenance.getPrice(), rate);

            maintenance.setPrice(converted);
            maintenance.setCurrency(Currency.valueOf(currency));
            return maintenance;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @Operation(summary = "Create service")
    @PostMapping
    public void create(@RequestHeader HttpHeaders headers,
                       @Valid @RequestBody MaintenanceDto dto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Maintenance maintenance = maintenanceMapper.toObject(dto);
            maintenanceService.create(maintenance, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @Operation(summary = "Update service")
    @PutMapping("/{id}")
    public void update(@RequestHeader HttpHeaders headers,
                       @PathVariable int id,
                       @Valid @RequestBody MaintenanceDto dto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Maintenance maintenance = maintenanceMapper.toObject(id, dto);
            maintenanceService.update(maintenance, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Delete service")
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            maintenanceService.delete(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}

package com.company.smartgarage.controllers.rest;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.VisitMapper;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.dtos.VisitDto;
import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.VisitStatus;
import com.company.smartgarage.models.filters.VisitFilterOptions;
import com.company.smartgarage.services.contracts.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitRestController {
    private final VisitService visitService;
    private final AuthenticationHelper authenticationHelper;
    private final VisitMapper visitMapper;

    @Autowired
    public VisitRestController(VisitService visitService, AuthenticationHelper authenticationHelper, VisitMapper visitMapper) {
        this.visitService = visitService;
        this.authenticationHelper = authenticationHelper;
        this.visitMapper = visitMapper;
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @Operation(summary = "Get all visits",
            description = "Retrieve a list of all visits with option to filter and sort")
    @GetMapping
    public List<Visit> get (@RequestHeader HttpHeaders headers,
                            @RequestParam(required = false) Double minPrice,
                            @RequestParam(required = false) Double maxPrice,
                            @RequestParam(required = false) Currency currency,
                            @RequestParam(required = false) VisitStatus status,
                            @RequestParam(required = false) LocalDate startDate,
                            @RequestParam(required = false) LocalDate endDate,
                            @RequestParam(required = false) Integer page
                            ) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            VisitFilterOptions filterOptions = new VisitFilterOptions(
                    minPrice,
                    maxPrice,
                    currency,
                    status,
                    startDate,
                    endDate,
                    page);
            return visitService.get(filterOptions, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get visit by ID")
    @GetMapping("/{id}")
    public Visit getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return visitService.getById(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }  catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @Operation(summary = "Create visit")
    @PostMapping
    public void create(@RequestHeader HttpHeaders headers,
                       @Valid @RequestBody VisitDto dto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Visit visit = visitMapper.toObject(dto);

            visitService.create(visit, user, dto.getVehicleLicensePlate());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "409", description = "Duplicate fields")
    @Operation(summary = "Update visit")
    @PutMapping("/{id}")
    public void update(@RequestHeader HttpHeaders headers,
                       @PathVariable int id,
                       @Valid @RequestBody VisitDto dto){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Visit visit = visitService.getById(id, user);
            visit = visitMapper.toObject(visit, dto);

            visitService.update(visit, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Close visit")
    @PutMapping("/{id}/close")
    public void closeVisit(@RequestHeader HttpHeaders headers,
                           @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Visit visit = visitService.getById(id, user);

            visitService.closeVisit(visit, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

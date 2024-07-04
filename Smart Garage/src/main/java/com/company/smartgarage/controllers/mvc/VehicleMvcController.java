package com.company.smartgarage.controllers.mvc;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.VehicleMapper;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.dtos.FilterVehiclesDto;
import com.company.smartgarage.models.dtos.VehicleDto;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.filters.VehicleFilterOptions;
import com.company.smartgarage.services.contracts.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/vehicles")
public class VehicleMvcController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private final AuthenticationHelper authenticationHelper;
    private final ModelService modelService;
    private final BrandService brandService;
    private final VisitService visitService;
    private final UserService userService;

    @Autowired
    public VehicleMvcController(VehicleService vehicleService, VehicleMapper vehicleMapper, AuthenticationHelper authenticationHelper, ModelService modelService, BrandService brandService, VisitService visitService, UserService userService) {
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
        this.authenticationHelper = authenticationHelper;
        this.modelService = modelService;
        this.brandService = brandService;
        this.visitService = visitService;
        this.userService = userService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("loggedUser") != null;
    }

    @ModelAttribute("isCustomer")
    public boolean populateIsCustomer(HttpSession session) {
        if (session.getAttribute("userRole") == null) {
            return false;
        }
        return session.getAttribute("userRole").equals(UserRole.CUSTOMER);
    }

    @ModelAttribute("isEmployee")
    public boolean populateIsEmployee(HttpSession session) {
        if (session.getAttribute("userRole") == null) {
            return false;
        }
        return session.getAttribute("userRole").equals(UserRole.EMPLOYEE);
    }

    @ModelAttribute("isAdmin")
    public boolean populateIsAdmin(HttpSession session) {
        if (session.getAttribute("userRole") == null) {
            return false;
        }
        return session.getAttribute("userRole").equals(UserRole.ADMINISTRATOR);
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("filterOptions", new FilterVehiclesDto());
        List<Vehicle> vehicles = vehicleService.getAll();
        model.addAttribute("vehicles", vehicles);
        return "AllVehiclesView";
    }

    @PostMapping
    public String showAllVehicles(@ModelAttribute("filterOptions") FilterVehiclesDto filterDto,
                                  Model model,
                                  HttpSession session) {
        try {
            User authorizedUser = authenticationHelper.tryGetCurrentUser(session);
            VehicleFilterOptions filterOptions = mapFilters(filterDto);
            List<Vehicle> vehicles = vehicleService.get(filterOptions);
            model.addAttribute("filterOptions", filterDto);
            model.addAttribute("filteredVehicles", vehicles);
            model.addAttribute("showAllVehiclesUrl", "/vehicles");
            return "AllVehiclesView";
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException a) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, a.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String showSingleVehicle(@PathVariable int id, Model model, HttpSession session) {
        try {
            Vehicle vehicle = vehicleService.getById(id);
//            List<Visit> visits = vehicleService.getVisits(id, authenticationHelper.tryGetCurrentUser(session));
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("visits", vehicle.getVisits());
            model.addAttribute("loggedUser", authenticationHelper.tryGetCurrentUser(session));
            return "SingleVehicleView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}/update")
    public String showUpdateVehiclePage(@PathVariable int id,
                                        Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Vehicle vehicleToUpdate = vehicleService.getById(id);
            VehicleDto vehicleDto = vehicleMapper.toDto(vehicleToUpdate);
            model.addAttribute("vehicleId", id);
            model.addAttribute("vehicle", vehicleDto);
            model.addAttribute("brands", brandService.getAll());
            model.addAttribute("currentBrand", vehicleService.getById(id).getModel().getBrand());
            model.addAttribute("currentModel", vehicleService.getById(id).getModel());
            model.addAttribute("updateUrl", String.format("/vehicles/%d/update", id));
            return "EditVehicleView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping("/{id}/update")
    public String updateVehicle(@PathVariable int id,
                                @Valid @ModelAttribute("vehicle") VehicleDto vehicleDto,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {
        User currentUser;
        model.addAttribute("currentBrand", vehicleService.getById(id).getModel().getBrand());
        model.addAttribute("currentModel", vehicleService.getById(id).getModel());
        try {
            currentUser = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "EditVehicleView";
        }

        try {
            Vehicle vehicleToUpdate = vehicleMapper.fromDto(id, vehicleDto);
//            model.addAttribute("vehicleId", id);
            vehicleService.update(vehicleToUpdate, currentUser);
            return String.format("redirect:/vehicles/%d", id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (EntityDuplicateException e) {
            model.addAttribute("error", e.getMessage());
            return "EditVehicleView";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
    }

    @GetMapping("/new/{id}")
    public String showRegisterNewVehiclePage(Model model, @PathVariable int id) {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setOwnerId(id);
        model.addAttribute("vehicle", vehicleDto);
        model.addAttribute("brands", brandService.getAll());
        return "NewVehicleView"; // Return the view name instead of redirecting
    }

    @PostMapping("/new/{id}")
    public String registerVehicle(@Valid @ModelAttribute("vehicle") VehicleDto vehicleDto,
                                  @PathVariable int id,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("brands", brandService.getAll());
            return "NewVehicleView"; // Return the view name for the form with errors
        }

        try {
            Vehicle vehicle = vehicleMapper.fromDto(vehicleDto);
            vehicleService.create(vehicle, authenticationHelper.tryGetCurrentUser(session));
            return String.format("redirect:/users/%d", id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (AuthorizationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        } catch (EntityDuplicateException e) {
            model.addAttribute("error", e.getMessage());
            return "NewVehicleView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable int id, Model model, HttpSession session){
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        }catch (AuthorizationException e){
            return "AccessDeniedView";
        }

        try {
            vehicleService.delete(id, user);
            return "redirect:/auth/logout";
        }catch (EntityNotFoundException e){
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }catch (AuthorizationException e){
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @GetMapping("/models/{brandId}")
    @ResponseBody
    public List<com.company.smartgarage.models.Model> getModels(@PathVariable int brandId) {
    return modelService.findByBrandId(brandId);
    }

    private VehicleFilterOptions mapFilters(FilterVehiclesDto filterDto) {

        if (filterDto.getModelName().isEmpty()) {
            filterDto.setModelName(null);
        }
        if (filterDto.getBrandName().isEmpty()) {
            filterDto.setBrandName(null);
        }
        if(filterDto.getLicensePlate().isEmpty()) {
            filterDto.setLicensePlate(null);
        }
        if(filterDto.getVin().isEmpty()) {
            filterDto.setVin(null);
        }
//        if(filterDto.getMinYear() == 0) {
//            filterDto.setMinYear(null);
//        }
//        if(filterDto.getMaxYear() == 0) {
//            filterDto.setMaxYear(null);
//        }
        if(filterDto.getSortBy().isEmpty()) {
            filterDto.setSortBy(null);
        }
        if(filterDto.getOrderBy().isEmpty()) {
            filterDto.setOrderBy(null);
        }

        VehicleFilterOptions vehicleFilterOptions = new VehicleFilterOptions(
                filterDto.getModelName(),
                filterDto.getBrandName(),
                filterDto.getLicensePlate(),
                filterDto.getVin(),
                filterDto.getMinYear(),
                filterDto.getMaxYear(),
                filterDto.getSortBy(),
                filterDto.getOrderBy()
        );


        return vehicleFilterOptions;
    }

}

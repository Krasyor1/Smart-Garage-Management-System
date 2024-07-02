package com.company.smartgarage.controllers.mvc;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.VisitMapper;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.dtos.FilterVisitsDto;
import com.company.smartgarage.models.dtos.VisitDto;
import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.enums.VisitStatus;
import com.company.smartgarage.models.filters.VisitFilterOptions;
import com.company.smartgarage.services.contracts.MaintenanceService;
import com.company.smartgarage.services.contracts.VehicleService;
import com.company.smartgarage.services.contracts.VisitService;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/visits")
public class VisitMvcController {
    private final VisitService visitService;
    private final MaintenanceService maintenanceService;
    private final VehicleService vehicleService;
    private final AuthenticationHelper authenticationHelper;
    private final VisitMapper visitMapper;

    public VisitMvcController(VisitService visitService,
                              MaintenanceService maintenanceService,
                              VehicleService vehicleService,
                              AuthenticationHelper authenticationHelper,
                              VisitMapper visitMapper) {
        this.visitService = visitService;
        this.maintenanceService = maintenanceService;
        this.vehicleService = vehicleService;
        this.authenticationHelper = authenticationHelper;
        this.visitMapper = visitMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateAuthenticatedUser(HttpSession session) {
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

    @ModelAttribute("statuses")
    public VisitStatus[] statuses() {
        return VisitStatus.values();
    }

    @ModelAttribute("currencies")
    public Currency[] currencies() {
        return Currency.values();
    }

    @GetMapping
    public String showAllVisitsPage(@ModelAttribute("filterOptions") FilterVisitsDto filterVisitsDto,
                                    Model model,
                                    HttpSession httpSession) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(httpSession);
            List<Visit> allVisits = visitService.get(new VisitFilterOptions(), user);
            model.addAttribute("visits", allVisits);
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageCount", visitService.pageCount());
            model.addAttribute("filtered", false);
            return "VisitsView";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
    }

    @PostMapping
    public String showVisits(@ModelAttribute("filterOptions") FilterVisitsDto filterVisitsDto,
                             Model model,
                             HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            VisitFilterOptions filterOptions = mapFilters(filterVisitsDto);
            List<Visit> allVisits = visitService.get(filterOptions, user);
            model.addAttribute("visits", allVisits);
            model.addAttribute("currentPage", filterVisitsDto.getPage());
            model.addAttribute("pageCount", visitService.pageCount());
            model.addAttribute("filtered", true);
            return "VisitsView";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
    }

    @GetMapping("/{page}")
    public String showVisitsByPage(@ModelAttribute("filterOptions") FilterVisitsDto filterVisitsDto,
                                   Model model,
                                   HttpSession session,
                                   @PathVariable int page) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            VisitFilterOptions filterOptions = mapFilters(filterVisitsDto);
            filterOptions.setPage(Optional.of(page - 1));
            List<Visit> allVisits = visitService.get(filterOptions, user);
            model.addAttribute("visits", allVisits);
            model.addAttribute("currentPage", filterVisitsDto.getPage());
            model.addAttribute("pageCount", visitService.pageCount());
            model.addAttribute("filtered", false);
            return "VisitsView";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
    }

    @GetMapping("/report/{id}")
    public String showVisitReportPage(@PathVariable int id,
                                      Model model,
                                      HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Visit visit = visitService.getById(id, user);
            model.addAttribute("visit", visit);
            return "VisitView";
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }  catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/new")
    public String showVisitCreatePage(HttpSession session, Model model) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }

        List<Maintenance> services = maintenanceService.getAll();
        model.addAttribute("allServices", services);
        model.addAttribute("visit", new VisitDto());
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("plate", false);
        return "VisitCreateView";
    }

    @PostMapping("/new")
    public String createVisit(@Valid @ModelAttribute("visit") VisitDto visitDto,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
        List<Maintenance> services = maintenanceService.getAll();
        model.addAttribute("allServices", services);
        model.addAttribute("currencies", Currency.values());

        if (bindingResult.hasErrors()) {
            return "VisitCreateView";
        }


        try {
            Visit visit = visitMapper.toObject(visitDto);
            visitService.create(visit, user, visitDto.getVehicleLicensePlate());
            return "redirect:/visits";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        } catch (EntityDuplicateException | EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "VisitCreateView";
        }
    }

    @GetMapping("/new/vehicle/{licensePlate}")
    public String showVisitCreatePage(@PathVariable String licensePlate, HttpSession session, Model model) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }

        VisitDto visitDto = new VisitDto();
        try {
            visitDto.setVehicleLicensePlate(licensePlate);
        } catch (EntityNotFoundException e) {
            return "NotFoundView";
        }

        List<Maintenance> services = maintenanceService.getAll();
        model.addAttribute("allServices", services);
        model.addAttribute("visit", visitDto);
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("plate", true);
        return "VisitCreateView";
    }

    @GetMapping("/{id}/update")
    public String showVisitUpdatePage(@PathVariable int id, HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Visit visit = visitService.getById(id, user);
            VisitDto dto = visitMapper.toDto(visit);
            model.addAttribute("visit", dto);
            model.addAttribute("visitId", id);

            List<Maintenance> services = maintenanceService.getAll();
            model.addAttribute("allServices", services);

            return "VisitUpdateView";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
    }

    @PostMapping("/{id}/update")
    public String updateVisit(@PathVariable int id,
                              @Valid @ModelAttribute("visit") VisitDto dto,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }

        if (bindingResult.hasErrors()) {
            return "VisitUpdateView";
        }

        List<Maintenance> services = maintenanceService.getAll();
        model.addAttribute("allServices", services);

        try {
            Visit visit = visitService.getById(id, user);
            visit = visitMapper.toObject(visit, dto);
            visitService.update(visit, user);

            return "redirect:/visits";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        } catch (EntityNotFoundException e) {
            return "NotFoundView";
        } catch (EntityDuplicateException e) {
            model.addAttribute("error", e.getMessage());
            return "VisitUpdateView";
        }
    }

    @GetMapping("/{id}/close")
    public String closeVisit(@PathVariable int id,
                             HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            Visit visit = visitService.getById(id, user);
            visitService.closeVisit(visit, user);

            return "redirect:/visits";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        } catch (EntityNotFoundException e) {
            return "NotFoundView";
        }
    }
    private VisitFilterOptions mapFilters(FilterVisitsDto filterVisitsDto) {

        VisitFilterOptions visitFilterOptions = new VisitFilterOptions(
                filterVisitsDto.getMinPrice(),
                filterVisitsDto.getMaxPrice(),
                filterVisitsDto.getCurrency(),
                filterVisitsDto.getVisitStatus(),
                filterVisitsDto.getStartDate(),
                filterVisitsDto.getEndDate(),
                filterVisitsDto.getPage());

        if (filterVisitsDto.getMinPrice() == 0.0) {
            visitFilterOptions.setMinPrice(java.util.Optional.empty());
        }
        if (filterVisitsDto.getMaxPrice() == 0.0) {
            visitFilterOptions.setMaxPrice(java.util.Optional.empty());
        }

        return visitFilterOptions;
    }
}

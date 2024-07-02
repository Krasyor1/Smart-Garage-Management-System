package com.company.smartgarage.controllers.mvc;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.MaintenanceMapper;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.MaintenanceCategory;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.dtos.CurrencyDto;
import com.company.smartgarage.models.dtos.FilterMaintenanceDto;
import com.company.smartgarage.models.dtos.MaintenanceDto;
import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.services.contracts.MaintenanceService;
import com.company.smartgarage.services.contracts.MaintenanceCategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.company.smartgarage.helpers.RateHelper.calculatePrice;
import static com.company.smartgarage.helpers.RateHelper.getRate;

@Controller
@RequestMapping("/services")
public class MaintenanceMvcController {
    public static final String FROM_CURRENCY = "BGN";
    private final MaintenanceService maintenanceService;
    private final MaintenanceCategoryService categoryService;
    private final MaintenanceMapper maintenanceMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public MaintenanceMvcController(MaintenanceService maintenanceService,
                                    MaintenanceCategoryService categoryService, MaintenanceMapper maintenanceMapper,
                                    AuthenticationHelper authenticationHelper) {
        this.maintenanceService = maintenanceService;
        this.categoryService = categoryService;
        this.maintenanceMapper = maintenanceMapper;
        this.authenticationHelper = authenticationHelper;
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

    @ModelAttribute("allServices")  public List<Maintenance> getServices(){
        return maintenanceService.get(new MaintenanceFilterOptions());
    }

    @ModelAttribute("allCategories")
    public List<MaintenanceCategory> getCategories() {
        return categoryService.getAll();
    }
    @GetMapping
    public String showAllServices(@ModelAttribute("filterOptions") FilterMaintenanceDto filterMaintenanceDto,
                                  @ModelAttribute("currency") CurrencyDto currencyDto, Model model) {
        model.addAttribute("allCategoriesAndServices", categoryService.getAll());
        model.addAttribute("currencies", Currency.values());
        return "ServicesView";
    }
    @PostMapping
    public String showServices(@ModelAttribute("filterOptions") FilterMaintenanceDto filterMaintenanceDto,
                               @ModelAttribute("currency") CurrencyDto currencyDto,
                               Model model) {
        MaintenanceFilterOptions maintenanceFilterOptions = mapFilters(filterMaintenanceDto);

        String currency = currencyDto.getCurrency().toString();
        double rate = getRate(currency, FROM_CURRENCY);

        model.addAttribute("currencies", Currency.values());

        if (hasNoFilters(maintenanceFilterOptions)) {
            List<MaintenanceCategory> servicesAndCategories = categoryService.getAll();
            for (MaintenanceCategory category : servicesAndCategories) {
                List<Maintenance> categoryServices = category.getServices().stream().toList();

                calculatePrice(FROM_CURRENCY, categoryServices, rate);
            }
            model.addAttribute("allCategoriesAndServices", servicesAndCategories);
            return "ServicesView";
        }  else {
            List<Maintenance> services = maintenanceService.get(maintenanceFilterOptions);
            List<Maintenance> calculatedServices = calculatePrice(FROM_CURRENCY, services, rate);

            model.addAttribute("filteredServices", calculatedServices);
            return "ServiceFilteredView";
        }
    }

    @GetMapping("/new")
    public String showCreateServicePage(HttpSession httpSession, Model model) {
        try {
            authenticationHelper.tryGetCurrentUser(httpSession);
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }

        model.addAttribute("service", new MaintenanceDto());
        return "ServiceCreateView";
    }

    @PostMapping("/new")
    public String createService(@Valid @ModelAttribute("service") MaintenanceDto maintenanceDto,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession httpSession) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(httpSession);
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }

        if (bindingResult.hasErrors()) {
            return "ServiceCreateView";
        }

        try {
            Maintenance maintenance = maintenanceMapper.toObject(maintenanceDto);
            maintenanceService.create(maintenance, user);
            return "redirect:/services";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        } catch (EntityDuplicateException e) {
            model.addAttribute("error", e.getMessage());
            return "ServiceCreateView";
        }
    }

    @GetMapping("/{id}/update")
    public String showUpdateServicePage(@PathVariable int id, HttpSession httpSession, Model model) {
        try {
            authenticationHelper.tryGetCurrentUser(httpSession);
            Maintenance maintenance = maintenanceService.getById(id);
            MaintenanceDto dto = maintenanceMapper.toDto(maintenance);
            model.addAttribute("service", dto);
            model.addAttribute("serviceId", id);
            return "ServiceUpdateView";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
    }

    @PostMapping("/{id}/update")
    public String updateService(@PathVariable int id,
                                @Valid @ModelAttribute("service") MaintenanceDto dto,
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
            return "ServiceUpdateView";
        }

        try {
            Maintenance maintenance = maintenanceMapper.toObject(id, dto);
            maintenanceService.update(maintenance, user);
            return "redirect:/services";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        } catch (EntityNotFoundException e) {
            return "NotFoundView";
        } catch (EntityDuplicateException e) {
            model.addAttribute("error", e.getMessage());
            return "ServiceUpdateView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteService(@PathVariable int id,
                               HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        }
        try {
            maintenanceService.delete(id, user);
            return "redirect:/services";
        } catch (AuthorizationException e) {
            return "AccessDeniedView";
        } catch (EntityNotFoundException e) {
            return "NotFoundView";
        }
    }
    private MaintenanceFilterOptions mapFilters(FilterMaintenanceDto filterMaintenanceDto) {

        if (filterMaintenanceDto.getSearchByName().isEmpty()) {
            filterMaintenanceDto.setSearchByName(null);
        }
        if (filterMaintenanceDto.getOrder().isEmpty()) {
            filterMaintenanceDto.setOrder(null);
        }
        if(filterMaintenanceDto.getSort().isEmpty()) {
            filterMaintenanceDto.setSort(null);
        }

        MaintenanceFilterOptions maintenanceFilterOptions = new MaintenanceFilterOptions(
                filterMaintenanceDto.getSearchByName(),
                filterMaintenanceDto.getSearchByPrice(),
                filterMaintenanceDto.getServiceName(),
                filterMaintenanceDto.getMinPrice(),
                filterMaintenanceDto.getMaxPrice(),
                filterMaintenanceDto.getSort(),
                filterMaintenanceDto.getOrder()
        );
        if (filterMaintenanceDto.getMinPrice() == 0.0) {
            maintenanceFilterOptions.setMinPrice(java.util.Optional.empty());
        }
        if (filterMaintenanceDto.getMaxPrice() == 0.0) {
            maintenanceFilterOptions.setMaxPrice(java.util.Optional.empty());
        }
        if (filterMaintenanceDto.getSearchByPrice() == 0.0) {
            maintenanceFilterOptions.setSearchByPrice(java.util.Optional.empty());
        }

        return maintenanceFilterOptions;
    }

    private boolean hasNoFilters(MaintenanceFilterOptions filterOptions) {
        return filterOptions.getSearchByName().isEmpty()
                && filterOptions.getSearchByPrice().isEmpty()
                && filterOptions.getServiceName().isEmpty()
                && filterOptions.getMinPrice().isEmpty()
                && filterOptions.getMaxPrice().isEmpty()
                && filterOptions.getSortBy().isEmpty()
                && filterOptions.getOrderBy().isEmpty();
    }
}

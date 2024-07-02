package com.company.smartgarage.controllers.mvc;

import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.MaintenanceMapper;
import com.company.smartgarage.models.MaintenanceCategory;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.services.contracts.MaintenanceCategoryService;
import com.company.smartgarage.services.contracts.MaintenanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomePageMvcController {
    private final MaintenanceService maintenanceService;
    private final MaintenanceMapper maintenanceMapper;
    private final AuthenticationHelper authenticationHelper;
    private final MaintenanceCategoryService maintenanceCategoryService;

    @Autowired
    public HomePageMvcController(MaintenanceService maintenanceService,
                                 MaintenanceMapper maintenanceMapper,
                                 AuthenticationHelper authenticationHelper, MaintenanceCategoryService maintenanceCategoryService) {
        this.maintenanceService = maintenanceService;
        this.maintenanceMapper = maintenanceMapper;
        this.authenticationHelper = authenticationHelper;
        this.maintenanceCategoryService = maintenanceCategoryService;
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

    @ModelAttribute("allCategories")  public List<MaintenanceCategory> getCategories(){
        return maintenanceCategoryService.getAll();
    }
    @GetMapping
    public String showHomePage() {
        return "HomePageView";
    }
}

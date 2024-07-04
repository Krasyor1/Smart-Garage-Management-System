package com.company.smartgarage.controllers.mvc;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.UserMapper;
import com.company.smartgarage.models.MaintenanceCategory;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.dtos.CurrencyDto;
import com.company.smartgarage.models.dtos.FilterEmployeeDto;
import com.company.smartgarage.models.dtos.FilterMaintenanceDto;
import com.company.smartgarage.models.dtos.UserDto;
import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.services.contracts.UserService;
import com.company.smartgarage.services.contracts.VehicleService;
import com.company.smartgarage.services.contracts.VisitService;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserMvcController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;
    private final VehicleService vehicleService;
    private final VisitService visitService;

    @Autowired
    public UserMvcController(UserService userService,
                             UserMapper userMapper,
                             AuthenticationHelper authenticationHelper,
                             VehicleService vehicleService,
                             VisitService visitService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
        this.vehicleService = vehicleService;
        this.visitService = visitService;
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
        model.addAttribute("filterOptions", new FilterEmployeeDto());
        model.addAttribute("users", userService.getAll());
        return "AllUsersView";
    }


    @PostMapping
    public String showAllUsers(@ModelAttribute("filterOptions") FilterEmployeeDto filterDto,
                               Model model,
                               HttpSession session) {
        try {
            User authorizedUser = authenticationHelper.tryGetCurrentUser(session);
            EmployeeFilterOptions filterOptions = mapFilters(filterDto);
            List<User> users = userService.get(filterOptions, authorizedUser);
            model.addAttribute("filteredUsers", users);
            model.addAttribute("getAllUsersUrl", "/users");
            return "AllUsersView";
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException a) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, a.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String showSingleUser(@PathVariable int id, Model model, HttpSession session){
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User user = userService.getById(id);

            model.addAttribute("user", user);
            model.addAttribute("userVehicles", user.getVehicles().stream().distinct().collect(Collectors.toList()));

            return "UserView";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditUserPage(@PathVariable int id,
                                   Model model,
                                   HttpSession session) {
        try {
           User loggedUser = authenticationHelper.tryGetCurrentUser(session);
           User userToUpdate = userService.getById(id);
            if (!loggedUser.equals(userToUpdate)
                    && !(loggedUser.getUserRole().equals(UserRole.EMPLOYEE)
                    || loggedUser.getUserRole().equals(UserRole.ADMINISTRATOR))) {
                return "AccessDeniedView";
            }
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }


        try {
            User userToUpdate = userService.getById(id);
            UserDto userDto = userMapper.toDto(userToUpdate);
            userDto.setPassword("");
            model.addAttribute("userId", id);
            model.addAttribute("userDto", userDto);
            model.addAttribute("updateUrl", String.format("/users/%d/update", id));
            return "EditUserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("userDto") UserDto dto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {

        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }


        if (bindingResult.hasErrors()) {
            return "EditUserView";
        }

        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToUpdate = userMapper.fromDto(id,dto);
            if(userToUpdate.getPassword().equals("")){
                userToUpdate.setPassword(userService.getById(id).getPassword());
            }
            userService.update(userToUpdate,currentUser);
            return String.format("redirect:/users/%d", id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (EntityDuplicateException d) {
            bindingResult.rejectValue("email", "duplicate_email", d.getMessage());
            return "EditUserView";
        } catch (AuthorizationException a) {
            bindingResult.rejectValue("password", "incorrect_password", a.getMessage());
            return "EditUserView";
        }
    }

    private EmployeeFilterOptions mapFilters(FilterEmployeeDto filterEmployeeDto) {

        if (filterEmployeeDto.getUsername().isEmpty()) {
            filterEmployeeDto.setUsername(null);
        }
        if (filterEmployeeDto.getEmail().isEmpty()) {
            filterEmployeeDto.setEmail(null);
        }
        if(filterEmployeeDto.getNames().isEmpty()) {
            filterEmployeeDto.setNames(null);
        }
        if(filterEmployeeDto.getPhoneNumber().isEmpty()) {
            filterEmployeeDto.setPhoneNumber(null);
        }
        if(filterEmployeeDto.getVehicleModel().isEmpty()) {
            filterEmployeeDto.setVehicleModel(null);
        }
        if(filterEmployeeDto.getVehicleBrand().isEmpty()) {
            filterEmployeeDto.setVehicleBrand(null);
        }
        if(filterEmployeeDto.getVisitBetween().isEmpty()) {
            filterEmployeeDto.setVisitBetween(null);
        }
        if(filterEmployeeDto.getSortBy().isEmpty()) {
            filterEmployeeDto.setSortBy(null);
        }
        if(filterEmployeeDto.getSortOrder().isEmpty()) {
            filterEmployeeDto.setSortOrder(null);
        }

        EmployeeFilterOptions employeeFilterOptions = new EmployeeFilterOptions(
                filterEmployeeDto.getUsername(),
                filterEmployeeDto.getEmail(),
                filterEmployeeDto.getPhoneNumber(),
                filterEmployeeDto.getNames(),
                filterEmployeeDto.getVehicleModel(),
                filterEmployeeDto.getVehicleBrand(),
                filterEmployeeDto.getVisitBetween(),
                filterEmployeeDto.getSortBy(),
                filterEmployeeDto.getSortOrder()
        );


        return employeeFilterOptions;
    }

}

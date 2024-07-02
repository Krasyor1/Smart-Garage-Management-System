package com.company.smartgarage;

import com.company.smartgarage.models.*;
import com.company.smartgarage.models.dtos.UserDto;
import com.company.smartgarage.models.dtos.VehicleDto;
import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.enums.UserStatus;
import com.company.smartgarage.models.enums.VisitStatus;
import com.company.smartgarage.models.filters.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Helpers {

    public static Brand createMockBrand(){
        var mockBrand = new Brand();
        mockBrand.setId(1);
        mockBrand.setBrandName("TestBrand");

        return mockBrand;
    }

    public static Model createMockModel(){
        var mockModel = new Model();
        mockModel.setId(1);
        mockModel.setBrand(createMockBrand());
        mockModel.setModelName("TestName");

        return mockModel;
    }

    public static Vehicle createMockVehicle() {
        var mockVehicle = new Vehicle();
        mockVehicle.setId(1);
        mockVehicle.setModel(createMockModel());
        mockVehicle.setVin("00000000000000000");
        mockVehicle.setLicensePlate("CA0000AA");
        mockVehicle.setYear(2000);
        mockVehicle.setOwner(createMockUserAdmin());
        mockVehicle.setVisits(new HashSet<>());

        return mockVehicle;
    }

    public static User createMockUserAdmin(){
        var mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUserRole(UserRole.ADMINISTRATOR);
        mockUser.setUserStatus(UserStatus.ACTIVE);
        mockUser.setEmail("mock@admin.com");
        mockUser.setNames("Mock Names");
        mockUser.setUsername("mock_admin");
        mockUser.setPassword("MockPass");
        mockUser.setPhoneNumber("0888112233");

        return mockUser;
    }

    public static UserDto createUserAdminDto(){
        UserDto dto = new UserDto();
        dto.setUsername("mock_admin");
        dto.setNames("Mock Names");
        dto.setEmail("mock@admin.com");
        dto.setPassword("MockPass");
        dto.setUserRole(UserRole.ADMINISTRATOR);
        return dto;
    }



    public static User createMockUserCustomer(){
        var mockUser = new User();
        mockUser.setUserId(2);
        mockUser.setUserRole(UserRole.CUSTOMER);
        mockUser.setUserStatus(UserStatus.ACTIVE);
        mockUser.setEmail("custom@test.com");
        mockUser.setNames("Customer Names");
        mockUser.setUsername("test_customer");
        mockUser.setPassword("MockCustomerPass");
        mockUser.setPhoneNumber("0888998877");
        mockUser.setVehicles(new HashSet<>());

        return mockUser;
    }

    public static UserDto createUserCustomerDto(){
        UserDto dto = new UserDto();
        dto.setUsername("test_customer");
        dto.setNames("Customer Names");
        dto.setEmail("customer@test.com");
        dto.setPassword("MockCustomerPass");
        dto.setUserRole(UserRole.ADMINISTRATOR);
        return dto;
    }

    public static User createMockUserEmployee(){
        var mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUserRole(UserRole.EMPLOYEE);
        mockUser.setUserStatus(UserStatus.ACTIVE);
        mockUser.setEmail("mock@employee.com");
        mockUser.setNames("Mock Names");
        mockUser.setUsername("mock_employee");
        mockUser.setPassword("MockPass");
        mockUser.setPhoneNumber("0888112211");
        mockUser.setVehicles(new HashSet<>());

        return mockUser;
    }

    public static UserDto createUserEmployeeDto(){
        UserDto dto = new UserDto();
        dto.setUsername("test_employee");
        dto.setNames("Employee Names");
        dto.setEmail("employee@test.com");
        dto.setPassword("MockEmployeePass");
        dto.setUserRole(UserRole.EMPLOYEE);
        return dto;
    }

    public static VehicleDto createVehicleDto(){
        VehicleDto dto = new VehicleDto();
        dto.setLicensePlate("CA0000AA");
        dto.setVin("00000000000000000");
        dto.setModelId(createMockModel().getId());
        dto.setCreationYear(2000);
        dto.setOwnerId(createMockUserAdmin().getUserId());

        return dto;
    }

    public static VehicleFilterOptions createMockFilterOptions() {
        return new VehicleFilterOptions(
                "mockModel",
                "mockBrand",
                "CA0000AA",
                "00000000000000000",
                2000,
                2023,
                "sort",
                "order");
    }

    public static EmployeeFilterOptions createMockEmployeeOptions() {
        return new EmployeeFilterOptions(
                "mockUsername",
                "mockEmail",
                "mockNames",
                "mockPhone",
                "mockVehicleModel",
                "mockVehicleBrand",
                "mockVisitCount",
                "mockSortBy",
                "mockSortOrder");
    }

        public static CustomerFilterOptions createMockCustomerOptions() {
        return new CustomerFilterOptions("AA0000AA", "2022-03-15");
    }

    public static MaintenanceFilterOptions createMockMaintenanceFilterOptions() {
        return new MaintenanceFilterOptions(
                "Oil change",
                500.0,
                "Belt and hose replacement",
                200.0,
                600.0,
                "name",
                "desc"
        );
    }

    public static VisitFilterOptions createMockVisitFilterOptions() {
        return new VisitFilterOptions(
                300.0,
                600.0,
                Currency.EUR,
                VisitStatus.OPEN,
                LocalDate.of(2023,04,10),
                LocalDate.of(2023, 04, 15),
                0
        );
    }

   public static Visit createMockVisit() {
       var mockVisit = new Visit();
       mockVisit.setId(1);
       mockVisit.setCreationDate(LocalDate.now());
       mockVisit.setTotalPrice(55.5);
       mockVisit.setCurrency(Currency.BGN);
       mockVisit.setStatus(VisitStatus.OPEN);
       mockVisit.setServices(new HashSet<>());
       return mockVisit;
   }

    public static Maintenance createMockService() {
        var mockService = new Maintenance();
        mockService.setId(1);
        mockService.setPrice(55.5);
        mockService.setCurrency(Currency.BGN);
        mockService.setServiceName("Transmission Fluid Change");
        return mockService;
    }


    public static String toJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

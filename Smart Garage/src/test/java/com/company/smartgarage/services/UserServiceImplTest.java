package com.company.smartgarage.services;


import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.filters.CustomerFilterOptions;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;
import com.company.smartgarage.repositories.contracts.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.company.smartgarage.Helpers.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository mockRepository;
    @InjectMocks
    UserServiceImpl service;


    @Test
    public void create_Should_Throw_When_HasNoEmployeeAccess() {
        //Arrange
        EmployeeFilterOptions mockEmployeeOptions = createMockEmployeeOptions();
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.get(mockEmployeeOptions))
                .thenThrow(AuthorizationException.class);
        //Act, Assert
        Assertions.assertThrows(
                AuthorizationException.class,
                () -> service.get(mockEmployeeOptions, mockUser));

    }

    @Test
    void get_Should_CallRepository_When_HasEmployeeAccess() {
        //Arrange
        EmployeeFilterOptions mockEmployeeOptions = createMockEmployeeOptions();
        User mockUser = createMockUserEmployee();

        Mockito.when(mockRepository.get(mockEmployeeOptions))
                .thenReturn(null);
        // Act
        service.get(mockEmployeeOptions, mockUser);
        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .get(mockEmployeeOptions);

    }


    @Test
    public void get_Should_ReturnAllUsers_IfThereAreAny() {
        //Arrange
        User mockUser1 = createMockUserCustomer();
        User mockUser2 = createMockUserEmployee();
        List<User> users = new ArrayList<>();
        users.add(mockUser1);
        users.add(mockUser2);

        Mockito.when(mockRepository.getAll())
                .thenReturn(users);

        //Act
        List<User> output = service.getAll();
        //Assert
        Assertions.assertEquals(output, users);
    }

    @Test
    public void get_Should_Throw_When_UserWithIdDoesNotExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        //Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getById(mockUser.getUserId())
        );
    }

    @Test
    public void get_Should_ReturnUser_When_MatchByIdExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenReturn(mockUser);
        //Act
        User output = service.getById(mockUser.getUserId());
        //Assert
        Assertions.assertEquals(mockUser, output);
    }

    @Test
    public void get_Should_Throw_When_UserWithUsernameDoesNotExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getByUsername(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        //Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getByUsername(mockUser.getUsername())
        );
    }

    @Test
    public void get_Should_ReturnUser_When_UsernameExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getByUsername(Mockito.anyString()))
                .thenReturn(mockUser);
        //Act
        User output = service.getByUsername(mockUser.getUsername());
        //Assert
        Assertions.assertEquals(mockUser, output);
    }

    @Test
    public void get_Should_Throw_When_UserWithEmailDoesNotExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getByEmail(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        //Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getByEmail(mockUser.getEmail())
        );
    }

    @Test
    public void get_Should_ReturnUser_When_EmailExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getByEmail(Mockito.anyString()))
                .thenReturn(mockUser);
        //Act
        User output = service.getByEmail(mockUser.getEmail());
        //Assert
        Assertions.assertEquals(mockUser, output);
    }

    @Test
    public void get_Should_Throw_When_UserWithPhoneDoesNotExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getByPhone(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        //Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getByPhone(mockUser.getPhoneNumber())
        );
    }

    @Test
    public void get_Should_ReturnUser_When_PhoneExist() {
        //Arrange
        User mockUser = createMockUserCustomer();

        Mockito.when(mockRepository.getByPhone(Mockito.anyString()))
                .thenReturn(mockUser);
        //Act
        User output = service.getByPhone(mockUser.getPhoneNumber());
        //Assert
        Assertions.assertEquals(mockUser, output);
    }

    @Test
    public void create_Should_Throw_When_UserWithThisUsernameAlreadyExist() {
        //Arrange
        User mockUserToCreate = createMockUserCustomer();

        Mockito.when(mockRepository.getByUsername(Mockito.anyString()))
                .thenReturn(mockUserToCreate);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.create(mockUserToCreate));

    }

    @Test
    public void create_Should_Throw_When_UserWithThisEmailAlreadyExist() {
        //Arrange
        User mockUserToCreate = createMockUserCustomer();

        Mockito.lenient().when(mockRepository.getByEmail(Mockito.anyString()))
                .thenReturn(mockUserToCreate);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.create(mockUserToCreate));

    }

    @Test
    public void create_Should_Throw_When_UserWithThisPhoneAlreadyExist() {
        //Arrange
        User mockUserToCreate = createMockUserCustomer();

        Mockito.lenient().when(mockRepository.getByPhone(Mockito.anyString()))
                .thenReturn(mockUserToCreate);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.create(mockUserToCreate));

    }

    @Test
    public void create_Should_CallRepository_When_NoDuplicationsExist() {
        //Arrange
        User mockUserToCreate = createMockUserCustomer();

        Mockito.when(mockRepository.getByUsername(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        Mockito.when(mockRepository.getByEmail(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        Mockito.when(mockRepository.getByPhone(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);
        //Act
        service.create(mockUserToCreate);
        //Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(mockUserToCreate);

    }

    @Test
    public void update_Should_Throw_When_CreatorHasNoAccess() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserCustomer();

        //Act, Assert
        Assertions.assertThrows(
                AuthorizationException.class,
                () -> service.update(mockUserToUpdate, mockCreator));
    }

    @Test
    public void update_Should_Throw_When_UserWithThisUsernameAlreadyExist() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();
        User existingUser = createMockUserCustomer();
        existingUser.setUserId(3);

        Mockito.when(mockRepository.getByUsername(mockUserToUpdate.getUsername()))
                .thenReturn(existingUser);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.update(mockUserToUpdate, mockCreator));

    }

    @Test
    public void update_Should_Throw_When_UserWithThisEmailAlreadyExist() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();
        User existingUser = createMockUserCustomer();
        existingUser.setUserId(3);

        Mockito.when(mockRepository.getByEmail(mockUserToUpdate.getEmail()))
                .thenReturn(existingUser);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.update(mockUserToUpdate, mockCreator));
    }

    @Test
    public void update_Should_Throw_When_UserWithThisPhoneAlreadyExist() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();
        User existingUser = createMockUserCustomer();
        existingUser.setUserId(3);

        Mockito.when(mockRepository.getByPhone(mockUserToUpdate.getPhoneNumber()))
                .thenReturn(existingUser);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.update(mockUserToUpdate, mockCreator));
    }


    @Test
    public void update_Should_Throw_When_UserWithThisUsernameDoesNotExist() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();

        Mockito.when(mockRepository.getByUsername(mockUserToUpdate.getUsername()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(mockUserToUpdate, mockCreator));

    }

    @Test
    public void update_Should_Throw_When_UserWithThisEmailDoesNotExist() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();

        Mockito.when(mockRepository.getByEmail(mockUserToUpdate.getEmail()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(mockUserToUpdate, mockCreator));

    }

    @Test
    public void update_Should_Throw_When_UserWithThisPhoneDoesNotExist() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();

        Mockito.when(mockRepository.getByPhone(mockUserToUpdate.getPhoneNumber()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(mockUserToUpdate, mockCreator));

    }


    @Test
    void update_Should_CallRepository_When_UserIsAdminAndUserToUpdateExist() {
        //Arrange
        User mockUserToUpdate = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();
        User existingUser = createMockUserCustomer();

        Mockito.when(mockRepository.getByUsername(mockUserToUpdate.getUsername()))
                .thenReturn(existingUser);

        Mockito.when(mockRepository.getByEmail(mockUserToUpdate.getEmail()))
                .thenReturn(existingUser);

        Mockito.when(mockRepository.getByPhone(mockUserToUpdate.getPhoneNumber()))
                .thenReturn(existingUser);

        //Act
        service.update(mockUserToUpdate, mockCreator);

        // Act, Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockUserToUpdate);
    }

    @Test
    void closeAccount_Should_ThrowException_When_UserIsNotAdmin() {
        //Arrange
        User mockCreator = createMockUserCustomer();

        // Act, Assert
        Assertions.assertThrows(
                AuthorizationException.class,
                () -> service.closeAccount(1, mockCreator));
    }

    @Test
    void closeAccount_Should_ThrowException_When_UserToCloseAccountNotExist() {
        //Arrange
        User mockCreator = createMockUserAdmin();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenThrow(EntityNotFoundException.class);

        // Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.closeAccount(3, mockCreator));
    }


    @Test
    void closeAccount_Should_CallRepository_When_UserIsAdminAndUserToCloseAccountExist() {
        //Arrange
        User mockUserToCloseAccount = createMockUserCustomer();
        User mockCreator = createMockUserAdmin();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenReturn(mockUserToCloseAccount);
        //Act
        service.closeAccount(mockUserToCloseAccount.getUserId(), mockCreator);
        //Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .closeAccount(mockUserToCloseAccount.getUserId());
    }


    @Test
    void getCustomerVisits_Should_Throw_WhenUserIsNotVehicleOwnerOrHasNoVisitsOnThatDate() {
        //Arrange
        User mockUser = createMockUserCustomer();
        CustomerFilterOptions filterOptions = createMockCustomerOptions();

        Mockito.when(mockRepository.getCustomerVisits(filterOptions, mockUser))
                .thenThrow(EntityNotFoundException.class);

        //Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getCustomerVisits(filterOptions, mockUser)
        );
    }

    @Test
    void getCustomerVisits_Should_Throw_WhenVehicleHasNoVisits() {
        //Arrange
        User mockUser = createMockUserCustomer();
        Vehicle vehicle = createMockVehicle();
        mockUser.getVehicles().add(vehicle);
        CustomerFilterOptions filterOptions = createMockCustomerOptions();

        Mockito.when(mockRepository.getCustomerVisits(filterOptions, mockUser))
                .thenThrow(EntityNotFoundException.class);

        //Act, Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getCustomerVisits(filterOptions, mockUser)
        );
    }

    @Test
    void getCustomerVisits_Should_CallRepository_When_UserHasNoPermission() {
        //Arrange
        User mockUser = createMockUserEmployee();
        Vehicle vehicle = createMockVehicle();
        Visit visit = createMockVisit();
        vehicle.getVisits().add(visit);
        CustomerFilterOptions filterOptions = createMockCustomerOptions();

        Mockito.when(mockRepository.getCustomerVisits(filterOptions, mockUser))
                .thenReturn(vehicle.getVisits());

        //Act
        service.getCustomerVisits(filterOptions, mockUser);

        //Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .getCustomerVisits(filterOptions, mockUser);
    }

    @Test
    void getCustomerVisits_Should_CallRepository_When_UserIsVehicleOwner() {
        //Arrange
        User mockUser = createMockUserCustomer();
        Vehicle vehicle = createMockVehicle();
        Visit visit = createMockVisit();
        mockUser.getVehicles().add(vehicle);
        vehicle.getVisits().add(visit);
        CustomerFilterOptions filterOptions = createMockCustomerOptions();

        Mockito.when(mockRepository.getCustomerVisits(filterOptions, mockUser))
                .thenReturn(vehicle.getVisits());

        //Act
        service.getCustomerVisits(filterOptions, mockUser);

        //Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .getCustomerVisits(filterOptions, mockUser);
    }

    @Test
    void getVisitReport_Should_Throw_wheUserIsNotVehicleOwnerAndHasNoPermission() {
        //Arrange
        User mockUser = createMockUserCustomer();
        Vehicle vehicle = createMockVehicle();
        String date = "2023-05-05";

        //Act, Assert
        Assertions.assertThrows(
                AuthorizationException.class,
                () -> service.getVisitReport(mockUser, vehicle, date));
    }

    @Test
    void getVisitReport_Should_CallRepository_WhenUserHasPermission() {
        //Arrange
        User mockUser = createMockUserEmployee();
        Vehicle vehicle = createMockVehicle();
        String date = "2023-05-05";

        Mockito.when(mockRepository.getVisitReport(vehicle, mockUser, date))
                .thenReturn("Visit report");
        //Act
        service.getVisitReport(mockUser, vehicle, date);

        //Act, Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .getVisitReport(vehicle, mockUser, date);
    }

    @Test
    void getVisitReport_Should_CallRepository_WhenUserIsVehicleOwner() {
        //Arrange
        User mockUser = createMockUserCustomer();
        Visit visit = createMockVisit();
        Vehicle vehicle = createMockVehicle();
        vehicle.getVisits().add(visit);
        mockUser.getVehicles().add(vehicle);
        String date = "2023-05-05";

        Mockito.when(mockRepository.getVisitReport(vehicle, mockUser, date))
                .thenReturn("Visit Report");
        //Act
        service.getVisitReport(mockUser, vehicle, date);

        //Act, Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .getVisitReport(vehicle, mockUser, date);
    }


}

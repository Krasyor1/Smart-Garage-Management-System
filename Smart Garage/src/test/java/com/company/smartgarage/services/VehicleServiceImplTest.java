package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.filters.VehicleFilterOptions;
import com.company.smartgarage.repositories.contracts.VehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.company.smartgarage.Helpers.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceImplTest {

    @Mock
    VehicleRepository mockRepository;

    @InjectMocks
    VehicleServiceImpl service;

    @Test
    void get_Should_CallRepository() {
        // Arrange
        VehicleFilterOptions mockFilterOptions = createMockFilterOptions();
//        Mockito.when(mockRepository.get(mockFilterOptions))
//                .thenReturn(null);

        // Act
        service.get(mockFilterOptions);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .get(mockFilterOptions);
    }

    @Test
    public void get_Should_ReturnVehicle_When_MatchByIdExist() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenReturn(mockVehicle);

        // Act
        Vehicle result = service.getById(mockVehicle.getId());

        // Assert
        Assertions.assertEquals(mockVehicle, result);
    }

    @Test
    public void create_Should_CallRepository_When_VehicleWithSameVINDoesNotExist() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockUser = createMockUserAdmin();

        Mockito.when(mockRepository.getByVin(mockVehicle.getVin()))
                .thenThrow(EntityNotFoundException.class);
        Mockito.when(mockRepository.getByLicensePlate(Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);

        // Act
        service.create(mockVehicle, mockUser);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(mockVehicle);
    }

    @Test
    public void create_Should_Throw_When_VehicleWithSameVINExists() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockUser = createMockUserAdmin();

        Mockito.when(mockRepository.getByVin(mockVehicle.getVin()))
                .thenReturn(mockVehicle);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.create(mockVehicle, mockUser));
    }

    @Test
    public void create_Should_Throw_When_UserIsNotAdminOrEmployee() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockUser = createMockUserCustomer();

        // Act, Assert
        Assertions.assertThrows(
                AuthorizationException.class,
                () -> service.create(mockVehicle, mockUser));
    }

    @Test
    void update_Should_CallRepository_When_UserIsAdminOrEmployee() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockUserAdmin = createMockUserAdmin();

        Mockito.when(mockRepository.getByVin(Mockito.anyString()))
                .thenReturn(mockVehicle);
        Mockito.when(mockRepository.getByLicensePlate(Mockito.anyString()))
                .thenReturn(mockVehicle);
        // Act
        service.update(mockVehicle, mockUserAdmin);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockVehicle);
    }

    @Test
    public void update_Should_CallRepository_When_UpdatingExistingVehicle() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockUser = mockVehicle.getOwner();

        Mockito.when(mockRepository.getByVin(mockVehicle.getVin()))
                .thenReturn(mockVehicle);
        Mockito.when(mockRepository.getByLicensePlate(Mockito.anyString()))
                .thenReturn(mockVehicle);
        // Act
        service.update(mockVehicle, mockUser);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockVehicle);
    }

    @Test
    public void update_Should_ThrowException_When_UserIsNotAdmin() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockUser = createMockUserCustomer();

        // Act, Assert
        Assertions.assertThrows(
                AuthorizationException.class,
                () -> service.update(mockVehicle, mockUser));
    }

    @Test
    public void update_Should_ThrowException_When_VehicleWithSameVINandDifferentIdExists() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockAdmin = createMockUserAdmin();

        Vehicle mockExistingVehicle = createMockVehicle();
        mockExistingVehicle.setId(2);

        Mockito.when(mockRepository.getByVin(mockVehicle.getVin()))
                .thenReturn(mockExistingVehicle);

        // Act, Assert
        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> service.update(mockVehicle, mockAdmin));
    }

    @Test
    void delete_Should_CallRepository_When_UserIsAdmin() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();
        User mockAdmin = createMockUserAdmin();

        // Act
        service.delete(1, mockAdmin);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .delete(1);
    }

    @Test
    void delete_Should_ThrowException_When_UserIsNotAdmin() {
        // Arrange
        Vehicle mockVehicle = createMockVehicle();

        User mockUser = createMockUserCustomer();

        // Act, Assert
        Assertions.assertThrows(
                AuthorizationException.class,
                () -> service.delete(1, mockUser));
    }


}

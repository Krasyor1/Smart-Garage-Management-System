package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.repositories.contracts.MaintenanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static com.company.smartgarage.Helpers.*;

@ExtendWith(MockitoExtension.class)
public class MaintenanceServiceImplTest {

    @Mock
    MaintenanceRepository mockRepository;

    @InjectMocks
    MaintenanceServiceImpl maintenanceService;

    @Test
    void getAll_Should_CallRepository() {
        maintenanceService.getAll();
        Mockito.verify(mockRepository, Mockito.times(1)).getAll();
    }

    @Test
    void get_Should_CallRepository() {
        MaintenanceFilterOptions filterOptions = createMockMaintenanceFilterOptions();
        maintenanceService.get(filterOptions);
        Mockito.verify(mockRepository, Mockito.times(1)).get(filterOptions);
    }

    @Test
    void getById_Should_ReturnMaintenance_When_IdExists() {
        Maintenance maintenance = createMockService();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenReturn(maintenance);

        Maintenance result = maintenanceService.getById(maintenance.getId());

        Assertions.assertEquals(maintenance, result);
    }

    @Test
    void should_Throw_AuthorizationException_When_UserIsCustomer() {
        User mockUser = createMockUserCustomer();
        Maintenance mockMaintenance = createMockService();

        Assertions.assertAll(

                ()-> Assertions.assertThrows(AuthorizationException.class, () -> maintenanceService.create(mockMaintenance, mockUser)),
                ()-> Assertions.assertThrows(AuthorizationException.class, () -> maintenanceService.update(mockMaintenance, mockUser)),
                ()-> Assertions.assertThrows(AuthorizationException.class, () -> maintenanceService.delete(mockMaintenance.getId(), mockUser)));
    }

    @Test
    void create_Should_CallRepository() {
        Maintenance mockService = createMockService();
        User mockUser = createMockUserEmployee();

        Maintenance anotherMock = new Maintenance();
        anotherMock.setPrice(10);

        Mockito.when(mockRepository.getByServiceName(mockService.getServiceName(), true))
                        .thenThrow(new EntityNotFoundException("Service", "name", mockService.getServiceName()));

        Mockito.when(mockRepository.getByServiceName(mockService.getServiceName(), false))
                .thenThrow(new EntityNotFoundException("Service", "name", mockService.getServiceName()));
        maintenanceService.create(mockService, mockUser);

        Mockito.verify(mockRepository, Mockito.times(1)).create(mockService);
    }

    @Test
    void update_Should_CallRepository() {
        Maintenance mockService = createMockService();
        User mockUser = createMockUserEmployee();


        Mockito.when(mockRepository.getByServiceName(mockService.getServiceName(), true))
                .thenThrow(new EntityNotFoundException("Service", "name", mockService.getServiceName()));

        Mockito.when(mockRepository.getByServiceName(mockService.getServiceName(), false))
                .thenThrow(new EntityNotFoundException("Service", "name", mockService.getServiceName()));

        maintenanceService.update(mockService, mockUser);

        Mockito.verify(mockRepository, Mockito.times(1)).update(mockService);
    }

    @Test
    void delete_Should_CallRepository() {
        Maintenance mockService = createMockService();
        User mockUser = createMockUserEmployee();

        Mockito.when(mockRepository.getById(mockService.getId())).thenReturn(mockService);

        maintenanceService.delete(mockService.getId(), mockUser);

        Mockito.verify(mockRepository, Mockito.times(1)).update(mockService);
    }


}

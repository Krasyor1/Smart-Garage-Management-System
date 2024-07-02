package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.*;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.enums.VisitStatus;
import com.company.smartgarage.models.filters.VisitFilterOptions;
import com.company.smartgarage.repositories.contracts.VehicleRepository;
import com.company.smartgarage.repositories.contracts.VisitRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.company.smartgarage.Helpers.*;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplTest {

    @Mock
    VisitRepository visitRepository;
    @Mock
    VehicleRepository vehicleRepository;

    @InjectMocks
    VisitServiceImpl visitService;

    @Test
    void get_Should_ThrowAuthorizationException() {
        User mockUser = createMockUserCustomer();

        Assertions.assertThrows(AuthorizationException.class, ()-> visitService.get(new VisitFilterOptions(), mockUser));
    }

    @Test
    void get_Should_CallRepository() {
        User user = createMockUserEmployee();
        VisitFilterOptions filterOptions = new VisitFilterOptions();

        visitService.get(filterOptions, user);

        Mockito.verify(visitRepository, Mockito.times(1)).get(filterOptions);
    }

    @Test
    void getById_Should_ReturnVisit() {
        Visit visit = createMockVisit();

        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);

        Visit result = visitService.getById(visit.getId(), createMockUserEmployee());

        Assertions.assertEquals(result, visit);
    }

    @Test
    void getById_Should_ThrowAuthorizationException() {
        Visit visit = createMockVisit();

        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);

        Assertions.assertThrows(AuthorizationException.class,
                ()-> visitService.getById(visit.getId(), createMockUserCustomer()));
    }

    @Test
    void create_Should_CallRepository() {
        Vehicle vehicle = createMockVehicle();
        String licensePlate = vehicle.getLicensePlate();
        Visit mockVisit = createMockVisit();
        User user = createMockUserEmployee();

        Mockito.when(visitRepository.create(mockVisit)).thenReturn(mockVisit);
        Mockito.when(vehicleRepository.getByLicensePlate(licensePlate)).thenReturn(vehicle);
        visitService.create(mockVisit, user, licensePlate);

        Mockito.verify(visitRepository, Mockito.times(1)).create(mockVisit);
        Mockito.verify(visitRepository, Mockito.times(1)).addServices(mockVisit.getServices());
    }

    @Test
    void update_Should_ThrowAuthorizationException_When_StatusIsClosed() {
        Visit mockVisit = createMockVisit();
        mockVisit.setStatus(VisitStatus.CLOSED);

        Assertions.assertThrows(AuthorizationException.class, ()-> visitService.update(mockVisit, createMockUserEmployee()));
    }

    @Test
    void update_Should_CallRepository() {
        Visit mockVisit = createMockVisit();
        visitService.update(mockVisit, createMockUserEmployee());

        Mockito.verify(visitRepository, Mockito.times(1)).update(mockVisit);
    }

    @Test
    void closeVisit_Should_CallRepository() {
        Visit mockVisit = createMockVisit();
        visitService.closeVisit(mockVisit, createMockUserEmployee());

        Mockito.verify(visitRepository, Mockito.times(1)).update(mockVisit);
    }
}

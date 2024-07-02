package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.models.DoneService;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.enums.VisitStatus;
import com.company.smartgarage.models.filters.VisitFilterOptions;
import com.company.smartgarage.repositories.contracts.VehicleRepository;
import com.company.smartgarage.repositories.contracts.VisitRepository;
import com.company.smartgarage.services.contracts.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

@Service
public class VisitServiceImpl implements VisitService {
    public static final String UNAUTHORIZED = "User %s is unauthorized to %s this visit";
    public static final String CANNOT_BE_EDITED_AFTER_BEING_CLOSED = "Visit cannot be edited after being closed";
    private final VisitRepository visitRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository, VehicleRepository vehicleRepository) {
        this.visitRepository = visitRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Visit> get(VisitFilterOptions filterOptions, User user) {
        checkPermission(user, "view");
        return visitRepository.get(filterOptions);
    }

    @Override
    public Visit getById(int id, User user) {
        Visit visit = visitRepository.getById(id);
        checkPermission(visit, user);
        return visit;
    }

    @Override
    public void create(Visit visit, User user, String licensePlate) {
        checkPermission(user, "create");
        visit.setStatus(VisitStatus.OPEN);
        visit.setCreationDate(LocalDate.now());

        visit.setTotalPrice(calculatePrice(visit.getServices()));
        Vehicle vehicle = vehicleRepository.getByLicensePlate(licensePlate);
        visit.setVehicle(vehicle);

        visitRepository.create(visit);
        visitRepository.addServices(visit.getServices());
    }

    @Override
    public void update(Visit visit, User user) {
        if (visit.getStatus() == VisitStatus.CLOSED) {
            throw new AuthorizationException(CANNOT_BE_EDITED_AFTER_BEING_CLOSED);
        }
        checkPermission(user, "edit");

        visit.setTotalPrice(calculatePrice(visit.getServices()));
        visitRepository.update(visit);
    }

    @Override
    public void closeVisit(Visit visit, User user) {
        checkPermission(user, "close");
        visit.setStatus(VisitStatus.CLOSED);
        visitRepository.update(visit);

    }

    @Override
    public long pageCount() {
        long numberOfPages = 0;
        long numberOfVisits = visitRepository.visitCount();

        if (numberOfVisits % 10 == 0) {
            numberOfPages = numberOfVisits / 10;
        } else {
            numberOfPages = (numberOfVisits / 10) + 1;
        }

        return numberOfPages;
    }

    private double calculatePrice(Set<DoneService> services) {
        DecimalFormat df = new DecimalFormat("#.##");
        double totalPrice = 0;
        for (DoneService service : services) {
            totalPrice += service.getServicePrice();
        }
        totalPrice = Double.parseDouble(df.format(totalPrice));
        return totalPrice;
    }
    private void checkPermission(Visit visit, User user) {
        if (isNotOwnerOfVehicle(visit, user) && user.getUserRole() == UserRole.CUSTOMER) {
            throw new AuthorizationException(format(UNAUTHORIZED, user.getUsername(), "view"));
        }
    }

    private void checkPermission(User user, String action) {
        if (user.getUserRole() == UserRole.CUSTOMER) {
            throw new AuthorizationException(format(UNAUTHORIZED, user.getUsername(), action));
        }
    }

    private boolean isNotOwnerOfVehicle(Visit visit, User user) {
        return user.getVehicles().stream().noneMatch(vehicle -> vehicle.getVisits().contains(visit));
    }
}

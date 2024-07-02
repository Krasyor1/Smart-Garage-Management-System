package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.repositories.contracts.MaintenanceRepository;
import com.company.smartgarage.services.contracts.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {
    public static final String UNAUTHORIZED = "User %s is unauthorized to %s a service";
    private final MaintenanceRepository repository;

    @Autowired
    public MaintenanceServiceImpl(MaintenanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Maintenance> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Maintenance> get(MaintenanceFilterOptions filterOptions) {
        return repository.get(filterOptions);
    }

    @Override
    public Maintenance getByServiceName(String serviceName) {
        return repository.getByServiceName(serviceName, true);
    }

    @Override
    public Maintenance getById(int id) {
        return repository.getById(id);
    }

    @Override
    public void create(Maintenance maintenance, User user) {
        handleAuthorizationException(user, "create");

        try {
            handleEntityDuplication(maintenance, "create");
        } catch (EntityNotFoundException e) {
            repository.create(maintenance);
        }
    }

    @Override
    public void update(Maintenance maintenance, User user) {
        handleAuthorizationException(user, "update");
        try {
            handleEntityDuplication(maintenance, "update");
        } catch (EntityNotFoundException e) {
            repository.update(maintenance);
        }
    }

    @Override
    public void delete(int id, User user) {
        handleAuthorizationException(user, "delete");
        Maintenance maintenance = repository.getById(id);
        maintenance.setAvailable(false);
        repository.update(maintenance);
       // repository.delete(id);
    }

    private void handleAuthorizationException(User user, String action) {
        if(user.getUserRole() == UserRole.CUSTOMER) {
            throw new AuthorizationException(format(UNAUTHORIZED, user.getNames(), action));
        }
    }


    private void handleEntityDuplication(Maintenance maintenance, String action) {
        Maintenance duplicate;
        try {
            duplicate = repository.getByServiceName(maintenance.getServiceName(), true);
            if (action.equals("create") || duplicate.getPrice() == maintenance.getPrice()) {
                throw new EntityDuplicateException("Service", "name", maintenance.getServiceName());
            }

        } catch (EntityNotFoundException e) {
            duplicate = repository.getByServiceName(maintenance.getServiceName(), false);
        }
        duplicate.setPrice(maintenance.getPrice());
        repository.update(duplicate);
    }
}

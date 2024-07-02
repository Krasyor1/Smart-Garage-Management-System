package com.company.smartgarage.helpers;

import com.company.smartgarage.models.DoneService;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.dtos.VisitDto;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.services.contracts.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.company.smartgarage.helpers.RateHelper.calculatePrice;
import static com.company.smartgarage.helpers.RateHelper.getRate;

@Component
public class VisitMapper {
    public static final String FROM_CURRENCY = "BGN";
    private final MaintenanceService maintenanceService;

    @Autowired
    public VisitMapper(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }
    public Visit toObject(VisitDto dto) {
        Visit visit = new Visit();
        return toObject(visit, dto);
    }
    public Visit toObject(Visit visit, VisitDto dto) {

        mapServices(dto, visit);
        visit.setCurrency(dto.getCurrencyDto());

        return visit;
    }

    public VisitDto toDto(Visit visit) {
        VisitDto dto = new VisitDto();
        dto.setVehicleLicensePlate(visit.getVehicle().getLicensePlate());
        Set<Integer> dtoService = new HashSet<>();
        visit.getServices().stream().forEach(doneService -> dtoService.add(doneService.getServiceId()));
        dto.setCurrencyDto(visit.getCurrency());
        return dto;
    }

    private void mapServices(VisitDto dto, Visit visit) {
        Set<Integer> services = dto.getServices();

        if (isForVisitCreation(visit)) {
            visit.setServices(new HashSet<>());
        } else {
            removeServices(dto, visit);
        }

        List<Maintenance> allServices = maintenanceService.get(new MaintenanceFilterOptions());

        String targetCurrency = dto.getCurrencyDto().toString();

        double rate = getRate(targetCurrency, FROM_CURRENCY);

        for (int service : services) {
            for (Maintenance maintenance : allServices) {
                if (maintenance.getId() == service) {
                    double price = calculatePrice(FROM_CURRENCY, maintenance.getPrice(), rate);
                    DoneService serviceToAdd = new DoneService();

                    serviceToAdd.setServiceId(service);
                    serviceToAdd.setServiceName(maintenance.getServiceName());
                    serviceToAdd.setServicePrice(price);
                    serviceToAdd.setVisit(visit);

                    visit.getServices().add(serviceToAdd);
                    break;
                }
            }
        }
    }

    private void removeServices (VisitDto dto, Visit visit) {

        Set<Integer> dtoServices = dto.getServices();

        List<DoneService> visitServices = visit.getServices().stream().toList();

        String previousCurrency = visit.getCurrency().toString();
        String targetCurrency = dto.getCurrencyDto().toString();

        double rate = getRate(targetCurrency, previousCurrency);

        for (DoneService service : visitServices) {

            if (!dtoServices.contains(service.getServiceId())) {
                visit.getServices().remove(service);
            } else if (!targetCurrency.equals(previousCurrency)) {
                double price = service.getServicePrice();
                double newPrice = calculatePrice(previousCurrency, price, rate);
                service.setServicePrice(newPrice);
            }
        }
    }

    private boolean isForVisitCreation(Visit visit) {
        return visit.getServices() == null;
    }
}

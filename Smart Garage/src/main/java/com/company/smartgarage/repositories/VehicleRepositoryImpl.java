package com.company.smartgarage.repositories;

import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.Vehicle;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.dtos.FilterEmployeeDto;
import com.company.smartgarage.models.dtos.FilterVehiclesDto;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;
import com.company.smartgarage.models.filters.VehicleFilterOptions;
import com.company.smartgarage.repositories.contracts.VehicleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VehicleRepositoryImpl extends BaseCrudRepositoryImpl<Vehicle> implements VehicleRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public VehicleRepositoryImpl(SessionFactory sessionFactory, SessionFactory sessionFactory1) {
        super(Vehicle.class, sessionFactory);
        this.sessionFactory = sessionFactory1;
    }

    @Override
    public List<Vehicle> get(VehicleFilterOptions filterOptions) {
        try(Session session = sessionFactory.openSession()) {
            List<String>filters = new ArrayList<>();
            Map<String, Object> queryParams = new HashMap<>();

            filterOptions.getBrandName().ifPresent(b -> {
                filters.add(" v.model.brand.brandName like :brand ");
                queryParams.put("brand", String.format("%%%s%%", b));
            });

            filterOptions.getModelName().ifPresent(m -> {
                filters.add(" v.model.modelName = :model ");
                queryParams.put("model", String.format("%%%s%%", m));
            });

            filterOptions.getLicensePlate().ifPresent(l -> {
                filters.add(" v.licensePlate = :license ");
                queryParams.put("license", l);
            });

            filterOptions.getVin().ifPresent(v -> {
                filters.add(" v.vin = :vin ");
                queryParams.put("vin", v);
            });

            filterOptions.getMinYear().ifPresent(y -> {
                filters.add(" v.year >= :year ");
                queryParams.put("year", y);
            });

            filterOptions.getMaxYear().ifPresent(y -> {
                filters.add(" v.year <= :year ");
                queryParams.put("year", y);
            });

            StringBuilder queryBuilder = new StringBuilder(" from Vehicle v ");
            if(!filters.isEmpty()){
                queryBuilder.append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryBuilder.append(generateOrderBy(filterOptions));

            Query<Vehicle> serviceQuery = session.createQuery(queryBuilder.toString(), Vehicle.class);
            serviceQuery.setProperties(queryParams);

            return  serviceQuery.list();
        }
    }

    private String generateOrderBy(VehicleFilterOptions filterOptions) {
        if(filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy= switch (filterOptions.getSortBy().get()) {
            case "modelName" -> "v.model.modelName";
            case "brandName" -> "v.model.brand.brandName";
            case "licensePlate" -> "v.licensePlate";
            case "vin" -> "v.vin";
            case "year" -> "v.year";
            default -> "";
        };

        if (!orderBy.isEmpty() ) {
            orderBy = String.format(" order by %s", orderBy);
        }

        if (filterOptions.getOrderBy().isPresent() && filterOptions.getOrderBy().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }


        return orderBy;
    }

    @Override
    public Vehicle getByLicensePlate(String license) {
        try(Session session = sessionFactory.openSession()) {
            Query<Vehicle> query = session.createQuery("from Vehicle where licensePlate = :license", Vehicle.class);
            query.setParameter("license", license);

            List<Vehicle> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("Vehicle", "license plate", license);
            }
            return result.get(0);
        }
    }

    @Override
    public Vehicle getByVin(String vin) {
        try(Session session = sessionFactory.openSession()) {
            Query<Vehicle> query = session.createQuery("from Vehicle where vin = :vin", Vehicle.class);
            query.setParameter("vin", vin);

            List<Vehicle> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("Vehicle", "vin", vin);
            }
            return result.get(0);
        }
    }

   @Override
   public List<Vehicle> getByPhoneNumber(String phone) {
       try(Session session = sessionFactory.openSession()) {
           Query<Vehicle> query = session.createQuery("SELECT u.vehicles from User u join u.vehicles where u.phoneNumber = :phone", Vehicle.class);
           query.setParameter("phone", phone);

           List<Vehicle> result = query.list();
           if (result.size() == 0) {
               throw new EntityNotFoundException("Vehicle", "phone number", phone);
           }
           return result;
       }
   }

    @Override
    public List<Visit> getVisits(int id) {
        try(Session session = sessionFactory.openSession()) {
            Query<Visit> query = session.createQuery("SELECT visits from Vehicle where id = :id", Visit.class);
            query.setParameter("id", id);

            List<Visit> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("Vehicle", id);
            }
            return result;
        }
    }



}

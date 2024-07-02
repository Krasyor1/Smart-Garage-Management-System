package com.company.smartgarage.repositories;

import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.Maintenance;
import com.company.smartgarage.models.filters.MaintenanceFilterOptions;
import com.company.smartgarage.repositories.contracts.MaintenanceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Repository
public class MaintenanceRepositoryImpl extends BaseCrudRepositoryImpl<Maintenance> implements MaintenanceRepository {

    public static final String DOES_NOT_EXIST = "Service %s does not exist";
    public static final String GET_BY_NAME_QUERY = "from Maintenance where isAvailable = :condition and serviceName = :name";
    private final SessionFactory sessionFactory;

    @Autowired
    public MaintenanceRepositoryImpl(SessionFactory sessionFactory) {
        super(Maintenance.class, sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Maintenance> get(MaintenanceFilterOptions filterOptions) {
        try(Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> queryParams = new HashMap<>();

            filters.add(" isAvailable = true ");

            //TODO check if needed
       /*     filterOptions.getServiceName().ifPresent(s -> {
                filters.add(" serviceName = :service ");
                queryParams.put("service", s);
            }); */

            filterOptions.getSearchByName().ifPresent(value -> {
                if(!value.equals("")) {
                    filters.add(" serviceName like :name ");
                    queryParams.put("name", "%" + value + "%");
                }
            });

            if (filterOptions.getMinPrice().isPresent() && filterOptions.getMaxPrice().isPresent()) {
                filters.add(" price between :priceMin ");
                queryParams.put("priceMin", filterOptions.getMinPrice().get());
                filters.add(" :priceMax ");
                queryParams.put("priceMax", filterOptions.getMaxPrice().get());
            } else {
                filterOptions.getMinPrice().ifPresent(price -> {
                    filters.add(" price >= :price ");
                    queryParams.put("price", price);
                });

                filterOptions.getMaxPrice().ifPresent(price -> {
                    filters.add(" price <= :price ");
                    queryParams.put("price", price);
                });
            }



            filterOptions.getSearchByPrice().ifPresent(price -> {
                filters.add(" price = :price ");
                queryParams.put("price", price);
            });

            StringBuilder queryBuilder = new StringBuilder(" from Maintenance ");

            queryBuilder
                    .append(" where ")
                    .append(String.join(" and ", filters));

            queryBuilder.append(generateOrderBy(filterOptions));

            Query<Maintenance> serviceQuery = session.createQuery(queryBuilder.toString(), Maintenance.class);
            serviceQuery.setProperties(queryParams);

            return serviceQuery.list();
        }
    }

    @Override
    public Maintenance getByServiceName(String serviceName, boolean condition) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(GET_BY_NAME_QUERY, Maintenance.class)
                    .setParameter("condition", condition)
                    .setParameter("name", serviceName)
                    .uniqueResultOptional()
                    .orElseThrow(() -> new EntityNotFoundException(format(DOES_NOT_EXIST, serviceName)));
        }
    }

    private String generateOrderBy(MaintenanceFilterOptions filterOptionsPost) {

        StringBuilder sortBy= new StringBuilder();
        if (filterOptionsPost.getSortBy().isPresent()) {
            sortBy.append(" order by ").append(filterOptionsPost.getSortBy().get());

            if (filterOptionsPost.getOrderBy().isPresent()) {
                sortBy.append(" ").append(filterOptionsPost.getOrderBy().get());
            }
        }
        
        return sortBy.toString();
    }
}

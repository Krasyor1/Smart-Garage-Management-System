package com.company.smartgarage.repositories;

import com.company.smartgarage.models.DoneService;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.filters.VisitFilterOptions;
import com.company.smartgarage.repositories.contracts.VisitRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class VisitRepositoryImpl implements VisitRepository {

    public static final int PAGE_SIZE = 10;
    private final SessionFactory sessionFactory;
    private long visitCount;

    @Autowired
    public VisitRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Visit> get(VisitFilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> queryParams = new HashMap<>();

            filterOptions.getCurrency().ifPresent(currency -> {
                filters.add(" currency = :currency ");
                queryParams.put("currency", currency);
            });

            if (hasMinAndMaxPrice(filterOptions)) {
                filters.add(" totalPrice between :priceMin ");
                queryParams.put("priceMin", filterOptions.getMinPrice().get());
                filters.add(" :priceMax ");
                queryParams.put("priceMax", filterOptions.getMaxPrice().get());
            } else {
                filterOptions.getMinPrice().ifPresent(price -> {
                    filters.add(" totalPrice >= :price ");
                    queryParams.put("price", price);
                });

                filterOptions.getMaxPrice().ifPresent(price -> {
                    filters.add(" totalPrice <= :price ");
                    queryParams.put("price", price);
                });
            }

            if (hasStartAndEndDate(filterOptions)) {
                filters.add(" creationDate between :startDate ");
                queryParams.put("startDate", filterOptions.getStartDate().get());
                filters.add(" :endDate ");
                queryParams.put("endDate", filterOptions.getEndDate().get());
            } else {
                filterOptions.getStartDate().ifPresent(localDate -> {
                    filters.add(" creationDate >= :startDate ");
                    queryParams.put("startDate", filterOptions.getStartDate().get());
                });
                filterOptions.getEndDate().ifPresent(localDate -> {
                    filters.add(" creationDate <= :endDate ");
                    queryParams.put("endDate", filterOptions.getEndDate().get());
                });
            }

            filterOptions.getStatus().ifPresent(visitStatus -> {
                filters.add(" status = :status ");
                queryParams.put("status", visitStatus);
            });


            StringBuilder queryBuilder = new StringBuilder(" from Visit ");
            if (!filters.isEmpty()) {
                queryBuilder
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryBuilder.append(" order by creationDate desc, id desc ");

            Query<Visit> visitQuery = session.createQuery(queryBuilder.toString(), Visit.class);
            visitQuery.setProperties(queryParams);

            visitCount = visitQuery.list().stream().count();

            if (filterOptions.getPage().isPresent()) {
                visitQuery.setFirstResult(filterOptions.getPage().get() * PAGE_SIZE);
            } else {
                visitQuery.setFirstResult(0);
            }

            visitQuery.setMaxResults(PAGE_SIZE);


            return visitQuery.list();
        }
    }

    @Override
    public Visit getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Visit> visitQuery = session.createQuery("from Visit where id = :id");
            visitQuery.setParameter("id", id);
            return visitQuery.list().get(0);
        }
    }

    @Override
    public Visit create(Visit visit) {
        try(Session session = sessionFactory.openSession()) {
            session.save(visit);
            return visit;
        }
    }

    @Override
    public void update(Visit visit) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(visit);
            session.getTransaction().commit();
        }
    }

    @Override
    public long visitCount() {
        return visitCount;
    }

    @Override
    public void addServices(Set<DoneService> services) {
        try (Session session = sessionFactory.openSession()) {
            for (DoneService service : services) {
                session.save(service);
            }
        }
    }

    private boolean hasMinAndMaxPrice(VisitFilterOptions filterOptions) {
        return filterOptions.getMinPrice().isPresent() && filterOptions.getMaxPrice().isPresent();
    }

    private boolean hasStartAndEndDate(VisitFilterOptions filterOptions) {
        return filterOptions.getStartDate().isPresent() && filterOptions.getEndDate().isPresent();
    }
}

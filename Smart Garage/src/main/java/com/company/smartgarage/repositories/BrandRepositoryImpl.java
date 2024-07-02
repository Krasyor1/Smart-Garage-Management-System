package com.company.smartgarage.repositories;

import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.Brand;
import com.company.smartgarage.repositories.contracts.BrandRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BrandRepositoryImpl extends BaseCrudRepositoryImpl<Brand> implements BrandRepository {

    private final SessionFactory sessionFactory;

    public BrandRepositoryImpl(SessionFactory sessionFactory) {
        super(Brand.class, sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Brand getByName(String name) {
        try(Session session = sessionFactory.openSession()) {
            Query<Brand> query = session.createQuery("from Brand where brandName = :name", Brand.class);
            query.setParameter("name", name);

            List<Brand> result = query.list();
            if(result.size() == 0){
                throw new EntityNotFoundException("Brand", "name", name);
            }
            return result.get(0);
        }
    }
}

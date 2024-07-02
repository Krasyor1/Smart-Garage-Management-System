package com.company.smartgarage.repositories;

import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.Model;
import com.company.smartgarage.repositories.contracts.ModelRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ModelRepositoryImpl extends BaseCrudRepositoryImpl<Model> implements ModelRepository {

    private final SessionFactory sessionFactory;
    public ModelRepositoryImpl(SessionFactory sessionFactory) {
        super(Model.class, sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Model getByName(String name) {
        try(Session session = sessionFactory.openSession()) {
            Query<Model> query = session.createQuery("from Model where modelName = :name", Model.class);
            query.setParameter("name", name);

            List<Model> result = query.list();
            if(result.size() == 0){
                throw new EntityNotFoundException("Model", "name", name);
            }
            return result.get(0);
        }
    }

    @Override
    public List<Model> findByBrandId(int id) {
        try(Session session = sessionFactory.openSession()) {
            Query<Model> query = session.createQuery("from Model where brand.id = :id", Model.class);
            query.setParameter("id", id);

            List<Model> result = query.list();
            if(result.size() == 0){
                throw new EntityNotFoundException("Model", "brandId", Integer.toString(id));
            }
            return result;
        }
    }
}

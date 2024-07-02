package com.company.smartgarage.repositories;

import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.repositories.contracts.BaseCrudRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.lang.String.format;

@Repository
public abstract class BaseCrudRepositoryImpl<T> implements BaseCrudRepository<T> {
    public static final String ERROR_MESSAGE = "%s with ID %s not found";
    private final Class<T> elementClass;
    protected final SessionFactory sessionFactory;

    @Autowired
    public BaseCrudRepositoryImpl(Class<T> elementClass, SessionFactory sessionFactory) {
        this.elementClass = elementClass;
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(format("from %s", elementClass.getName()), elementClass).list();
        }
    }

    @Override
    public T getById(int id) {
        String query = format("from %s where id = :id", elementClass.getName());
        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery(query, elementClass)
                    .setParameter("id", id)
                    .uniqueResultOptional()
                    .orElseThrow(()-> new EntityNotFoundException(
                            format(ERROR_MESSAGE, elementClass.getSimpleName(), id)));
        }
    }

    @Override
    public void create(T entity) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        T elementToDelete = getById(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(elementToDelete);
            session.getTransaction().commit();
        }
    }
}

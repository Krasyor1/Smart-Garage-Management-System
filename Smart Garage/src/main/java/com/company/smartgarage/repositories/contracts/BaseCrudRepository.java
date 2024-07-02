package com.company.smartgarage.repositories.contracts;

import java.util.List;

public interface BaseCrudRepository<T> {
    List<T> getAll();
    T getById(int id);
    void create(T entity);
    void update(T entity);
    void delete(int id);
}

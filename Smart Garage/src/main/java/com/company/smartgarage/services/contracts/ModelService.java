package com.company.smartgarage.services.contracts;

import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Model;

import java.util.List;

public interface ModelService {

    Model getById(int id);
    Model create(Model modelToCreate, User user);
    Model update(Model modelToUpdate, User user);
    void delete(int id, User user);
    List<Model> getAll();
    Model getByName(String name);
    List<Model> findByBrandId(int id);
}

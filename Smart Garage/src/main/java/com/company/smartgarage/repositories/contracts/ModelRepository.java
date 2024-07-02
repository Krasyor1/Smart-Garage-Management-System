package com.company.smartgarage.repositories.contracts;

import com.company.smartgarage.models.Model;

import java.util.List;

public interface ModelRepository extends BaseCrudRepository<Model>{

    Model getByName(String name);

    List<Model> findByBrandId(int id);
}

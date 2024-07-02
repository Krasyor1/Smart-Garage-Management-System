package com.company.smartgarage.services.contracts;

import com.company.smartgarage.models.Brand;
import com.company.smartgarage.models.User;

import java.util.List;

public interface BrandService {
    Brand getById(int id);
    Brand create(Brand brandToCreate, User user);
    Brand update(Brand brandToUpdate, User user);
    void delete(int id, User user);
    List<Brand> getAll();
    Brand getByName(String name);
}

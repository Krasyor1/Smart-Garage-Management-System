package com.company.smartgarage.repositories.contracts;

import com.company.smartgarage.models.Brand;

public interface BrandRepository extends BaseCrudRepository<Brand>{
    Brand getByName(String name);
}

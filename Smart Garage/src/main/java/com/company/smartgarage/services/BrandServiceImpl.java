package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.Brand;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.repositories.contracts.BrandRepository;
import com.company.smartgarage.services.contracts.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    public static final String AUTHORIZED_ERROR = "Only administrators or employees can modify brands!";
    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Brand getById(int id) {
        return brandRepository.getById(id);
    }

    @Override
    public Brand create(Brand brandToCreate, User user) {
        if(!hasPermission(user)){
            throw new AuthorizationException(AUTHORIZED_ERROR);
        }

        boolean duplicateExists = true;

        try {
            brandRepository.getByName(brandToCreate.getBrandName());
        }catch (EntityNotFoundException e){
            duplicateExists = false;
        }

        if(duplicateExists){
            throw new EntityDuplicateException("Model", "name", brandToCreate.getBrandName());
        }

        brandRepository.create(brandToCreate);
        return brandToCreate;
    }

    @Override
    public Brand update(Brand brandToUpdate, User user) {
        hasPermission(user);
        brandRepository.update(brandToUpdate);
        return brandToUpdate;    }

    @Override
    public void delete(int id, User user) {
        hasPermission(user);
        brandRepository.delete(id);
    }

    @Override
    public List<Brand> getAll() {
        return brandRepository.getAll();
    }

    @Override
    public Brand getByName(String name) {
        return brandRepository.getByName(name);
    }

    private boolean hasPermission(User authorizedUser) {
        return (authorizedUser.getUserRole().equals(UserRole.ADMINISTRATOR)
                || authorizedUser.getUserRole().equals(UserRole.EMPLOYEE));
    }
}

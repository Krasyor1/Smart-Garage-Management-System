package com.company.smartgarage.services;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Model;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.repositories.contracts.ModelRepository;
import com.company.smartgarage.services.contracts.ModelService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ModelServiceImpl implements ModelService {

    public static final String AUTHORIZED_ERROR = "Only administrators or employees can modify models!";
    private final ModelRepository modelRepository;

    public ModelServiceImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public Model getById(int id) {
        return modelRepository.getById(id);
    }

    @Override
    public Model create(Model modelToCreate, User user) {
         if(!hasPermission(user)){
             throw new AuthorizationException(AUTHORIZED_ERROR);
         }

         boolean duplicateExists = true;

         try {
             modelRepository.getByName(modelToCreate.getModelName());
         }catch (EntityNotFoundException e){
             duplicateExists = false;
         }

         if(duplicateExists){
             throw new EntityDuplicateException("Model", "name", modelToCreate.getModelName());
         }

         modelRepository.create(modelToCreate);
         return modelToCreate;
    }

    @Override
    public Model update(Model modelToUpdate, User user) {
        hasPermission(user);
        modelRepository.update(modelToUpdate);
        return modelToUpdate;
    }

    @Override
    public void delete(int id, User user) {
        hasPermission(user);
        modelRepository.delete(id);
    }

    @Override
    public List<Model> getAll() {
        return modelRepository.getAll();
    }

    @Override
    public Model getByName(String name) {
        return modelRepository.getByName(name);
    }

    @Override
    public List<Model> findByBrandId(int id) {
        return modelRepository.findByBrandId(id);
    }

    private boolean hasPermission(User authorizedUser) {
        return (authorizedUser.getUserRole().equals(UserRole.ADMINISTRATOR)
                || authorizedUser.getUserRole().equals(UserRole.EMPLOYEE));
    }


}

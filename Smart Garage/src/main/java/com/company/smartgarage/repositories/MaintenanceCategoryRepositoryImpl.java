package com.company.smartgarage.repositories;

import com.company.smartgarage.models.MaintenanceCategory;
import com.company.smartgarage.repositories.contracts.MaintenanceCategoryRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MaintenanceCategoryRepositoryImpl extends BaseCrudRepositoryImpl<MaintenanceCategory> implements MaintenanceCategoryRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public MaintenanceCategoryRepositoryImpl(SessionFactory sessionFactory) {
        super(MaintenanceCategory.class, sessionFactory);
        this.sessionFactory = sessionFactory;
    }

}

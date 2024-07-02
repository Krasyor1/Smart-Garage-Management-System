package com.company.smartgarage.repositories.contracts;

import com.company.smartgarage.models.DoneService;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.filters.VisitFilterOptions;

import java.util.List;
import java.util.Set;


public interface VisitRepository {
    List<Visit> get(VisitFilterOptions filterOptions);
    Visit getById(int id);
    Visit create(Visit visit);
    void update(Visit visit);
    long visitCount();
    void addServices(Set<DoneService> services);
}

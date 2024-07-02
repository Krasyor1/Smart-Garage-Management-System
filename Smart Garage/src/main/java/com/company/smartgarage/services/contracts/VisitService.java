package com.company.smartgarage.services.contracts;

import com.company.smartgarage.models.User;
import com.company.smartgarage.models.Visit;
import com.company.smartgarage.models.filters.VisitFilterOptions;

import java.util.List;

public interface VisitService {
    List<Visit> get(VisitFilterOptions filterOptions, User user);
    Visit getById(int id, User user);
    void create(Visit visit, User user, String licensePlate);
    void update(Visit visit, User user);
    void closeVisit(Visit visit, User user);
    long pageCount();
}

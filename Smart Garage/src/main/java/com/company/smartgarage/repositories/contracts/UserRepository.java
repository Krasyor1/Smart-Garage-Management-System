package com.company.smartgarage.repositories.contracts;

import com.company.smartgarage.models.*;
import com.company.smartgarage.models.filters.CustomerFilterOptions;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;

import java.util.List;
import java.util.Set;

public interface UserRepository extends BaseCrudRepository<User> {

    List<User> get(EmployeeFilterOptions filterOptions);
    User getByUsername(String username);
    User getByEmail(String email);
    User getByPhone(String phone);
    Set<Visit> getCustomerVisits(CustomerFilterOptions filterOptions, User customer);
    String getVisitReport(Vehicle vehicle, User customer, String visitDate);
    void closeAccount(int id);
    User getByResetToken (String token);
    Token findToken(String token);
    Token createToken(Token token);

}

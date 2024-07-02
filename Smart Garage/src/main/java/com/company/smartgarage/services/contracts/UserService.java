package com.company.smartgarage.services.contracts;

import com.company.smartgarage.models.*;
import com.company.smartgarage.models.filters.CustomerFilterOptions;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> get(EmployeeFilterOptions filterOptions, User authorizedUser);
    User getById(int id);
    User create(User userToCreate);
    User update(User userToUpdate, User user);
    void closeAccount(int id, User user);
    List<User> getAll();
    User getByUsername(String username);
    User getByEmail(String email);
    User getByPhone(String phone);
    Set<Visit> getCustomerVisits(CustomerFilterOptions filterOptions, User customer);
    String getVisitReport(User customer, Vehicle vehicle, String visitDate);
    void updateResetToken(String token, String email);
    User findByResetToken (String token);
    void updatePassword(User user, String newPassword);
    Token findToken(String token);
    Token createToken(String token);


}

package com.company.smartgarage.repositories;

import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.models.*;
import com.company.smartgarage.models.enums.UserStatus;
import com.company.smartgarage.models.filters.CustomerFilterOptions;
import com.company.smartgarage.models.filters.EmployeeFilterOptions;
import com.company.smartgarage.repositories.contracts.MaintenanceRepository;
import com.company.smartgarage.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class UserRepositoryImpl extends BaseCrudRepositoryImpl<User> implements UserRepository {

    public static final String CUSTOMER_HAS_NO_VISITS_OR_CAR_IS_NOT_REGISTERED_TO_HIM = "Customer with username %s has no visit on this date or vehicle with this license plate is not registered to him";
    private final SessionFactory sessionFactory;
    private final MaintenanceRepository maintenanceRepository;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory, MaintenanceRepository maintenanceRepository) {
        super(User.class, sessionFactory);
        this.sessionFactory = sessionFactory;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public List<User> get(EmployeeFilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getUsername().ifPresent(value -> {
                filters.add("u.username like :username");
                params.put("username", String.format("%%%s%%", value));
            });

            filterOptions.getEmail().ifPresent(value -> {
                filters.add("u.email like :email");
                params.put("email", String.format("%%%s%%", value));
            });

            filterOptions.getNames().ifPresent(value -> {
                filters.add("u.names like :names");
                params.put("names", String.format("%%%s%%", value));
            });

            filterOptions.getPhoneNumber().ifPresent(value -> {
                filters.add("u.phoneNumber like :phoneNumber");
                params.put("phoneNumber", String.format("%%%s%%", value));
            });

            filterOptions.getVehicleModel().ifPresent(value -> {
                filters.add("v.model.brandName like :modelName");
                params.put("modelName", String.format("%%%s%%", value));
            });

            filterOptions.getVehicleBrand().ifPresent(value -> {
                filters.add("v.model.brand.brandName like :brandName");
                params.put("brandName", String.format("%%%s%%", value));
            });

            filterOptions.getVisitBetween().ifPresent(value -> {
                String[] dates = value.split("/");
                LocalDate startDate = LocalDate.parse(dates[0]);
                LocalDate endDate = LocalDate.parse(dates[1]);
                filters.add("vi.creationDate >= :startDate and vi.creationDate <= :endDate");
                params.put("startDate", startDate);
                params.put("endDate", endDate);
            });

            StringBuilder queryString;

            if (filterOptions.getVehicleModel().isPresent() || filterOptions.getVehicleBrand().isPresent()) {
                queryString = new StringBuilder("select u from User u join u.vehicles v");

                if (filterOptions.getVisitBetween().isPresent()) {
                    queryString.append(" join v.visits vi");
                }

                if (!filters.isEmpty()) {
                    queryString
                            .append(" where ")
                            .append(String.join(" and ", filters));
                }
            } else {
                queryString = new StringBuilder("select u from User u");

                if (filterOptions.getVisitBetween().isPresent()) {
                    queryString.append(" join u.vehicles v join v.visits vi");
                }

                if (!filters.isEmpty()) {
                    queryString
                            .append(" where ")
                            .append(String.join(" and ", filters));
                }

            }
            queryString.append(generateOrderBy(filterOptions));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }
    }


    private String generateOrderBy(EmployeeFilterOptions filterOptions) {

        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = switch (filterOptions.getSortBy().get()) {
            case "username" -> "u.username";
            case "email" -> "u.email";
            case "names" -> "u.names";
            case "phoneNumber" -> "u.phoneNumber";
            case "modelName" -> "v.modelName";
            case "brandName" -> "v.model.brand.brandName";
            case "creationDate" -> "vi.creationDate";
            default -> "";
        };


        if (!orderBy.isEmpty() ) {
            orderBy = String.format(" order by %s", orderBy);
        }

        if (filterOptions.getSortOrder().isPresent() && filterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            List<User> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return result.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);

            List<User> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("User", "email", email);
            }
            return result.get(0);
        }
    }

    @Override
    public User getByPhone(String phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery
                    ("from User where phoneNumber = :phoneNumber", User.class);
            query.setParameter("phoneNumber", phoneNumber);

            List<User> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("User", "phone number", phoneNumber);
            }
            return result.get(0);
        }
    }

    @Override
    public void closeAccount(int id) {
        User user = getById(id);
        user.setUserStatus(UserStatus.INACTIVE);
    }

    @Override
    public Set<Visit> getCustomerVisits(CustomerFilterOptions filterOptions, User customer) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getLicencePlate().ifPresent(value -> {
                filters.add("v.licensePlate like :licencePlate");
                params.put("licencePlate", String.format("%%%s%%", value));
            });

            filterOptions.getLocalDate().ifPresent(value -> {
                LocalDate date = LocalDate.parse(value);
                filters.add("vi.creationDate like :date");
                params.put("date", date);
            });

            StringBuilder queryString = new StringBuilder("select vi from User u join u.vehicles v join v.visits vi where u.id =: userId ");
            params.put("userId", customer.getUserId());
            if (!filters.isEmpty()) {
                queryString
                        .append(" and ")
                        .append(String.join(" and ", filters));
            }

            Query<Visit> query = session.createQuery(queryString.toString(), Visit.class);
            query.setProperties(params);

            if (query.list().isEmpty()) {
                throw new EntityNotFoundException(String.format(
                        CUSTOMER_HAS_NO_VISITS_OR_CAR_IS_NOT_REGISTERED_TO_HIM, customer.getUsername()));
            }

            return new HashSet<>(query.list());
        }


    }

    @Override
    public String getVisitReport(Vehicle vehicle, User customer, String visitDate) {
        try (Session session = sessionFactory.openSession()) {
            Map<String, Object> params = new HashMap<>();

            String queryString = """
                    select vi
                    from User u
                    join u.vehicles v
                    join v.visits vi
                    where u.id =: userId
                    and v.licensePlate = :licensePlate
                    and vi.creationDate = :creationDate
                    """;

            params.put("userId", customer.getUserId());
            params.put("licensePlate", vehicle.getLicensePlate());
            params.put("creationDate", LocalDate.parse(visitDate));

            Query<Visit> query = session.createQuery(queryString, Visit.class);
            query.setProperties(params);

            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("No registered visits on this vehicle");
            }

            Visit visit = query.list().get(0);

            StringBuilder report = new StringBuilder();

            report
                    .append("-------------CUSTOMER INFO-------------\n")
                    .append(String.format("Username: %s\n", customer.getUsername()))
                    .append(String.format("Email: %s\n", customer.getEmail()))
                    .append(String.format("Phone: %s\n", customer.getPhoneNumber()))
                    .append(String.format("Names: %s\n\n", customer.getNames()))
                    .append("-------------VEHICLE INFO-------------\n")
                    .append(String.format("Brand: %s\n", vehicle.getModel().getBrand().getBrandName()))
                    .append(String.format("Model: %s\n", vehicle.getModel().getModelName()))
                    .append(String.format("Year: %s\n", vehicle.getYear()))
                    .append(String.format("License plate: %s\n", vehicle.getLicensePlate()))
                    .append(String.format("VIN: %s\n\n", vehicle.getVin()))
                    .append("-------------VEHICLE'S VISITS-------------\n")
                    .append(String.format("Visit on: %s / Total price: (%.2f %s)\n",
                            visit.getCreationDate(), visit.getTotalPrice(), visit.getCurrency()));


        /*    visit.getServices().forEach((integer, aDouble) ->
                    report.
                            append(String.format("  * Service: %s (%.2f %s)\n",
                                    maintenanceRepository.getById(integer).getServiceName(),
                                    aDouble, visit.getCurrency().toString()))); */

            return report.toString();
        }
    }

    @Override
    public User getByResetToken(String token) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User u join u.token t where t.token like :token", User.class);
            query.setParameter("token", token);
            User result = query.uniqueResult();
            if (result != null) {
                return result;

            }
        }
        throw new EntityNotFoundException("User hasn't requested to reset password");
    }

    @Override
    public Token findToken(String token) {
        try (Session session = sessionFactory.openSession()) {
            Query<Token> query = session.createQuery("from Token where token like :token", Token.class);
            query.setParameter("token", token);
            Token result = query.uniqueResult();
            if (result != null) {
                return result;
            }
        }
        throw new EntityNotFoundException("No such token found");
    }

    @Override
    public Token createToken(Token token) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(token);
            session.getTransaction().commit();
        }
        return token;
    }
}


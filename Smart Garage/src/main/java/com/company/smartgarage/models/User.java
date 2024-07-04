package com.company.smartgarage.models;

import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Set;


@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "user_names")
    private String names;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Set<Vehicle> vehicles;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "user_token",
            joinColumns =
                    { @JoinColumn(name = "user_id", referencedColumnName = "user_id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "token_id", referencedColumnName = "token_id") })
    private Token token;

    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", names='" + names + '\'' +
                ", userRole=" + userRole +
                ", userStatus=" + userStatus +
                '}';
    }

}

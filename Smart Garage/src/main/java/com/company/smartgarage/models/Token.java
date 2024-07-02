package com.company.smartgarage.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private int id;
    @Column(name = "token")
    private String token;
    @Column(name = "token_creation_date")
    private LocalDateTime tokenCreationDate;
    @JsonIgnore
    @OneToOne(mappedBy = "token")
    private User user;
}

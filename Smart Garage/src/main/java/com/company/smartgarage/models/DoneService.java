package com.company.smartgarage.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
@Table(name = "visit_service")
public class DoneService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_service_id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;
    @Column(name = "service_id")
    private int serviceId;
    @Column(name = "service_name")
    private String serviceName;
    @Column(name = "service_price")
    private double servicePrice;
}



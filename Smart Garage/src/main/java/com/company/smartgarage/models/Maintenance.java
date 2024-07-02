package com.company.smartgarage.models;

import com.company.smartgarage.models.enums.Currency;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Objects;

@Entity
@Data
@Table(name = "services" )
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int id;
    @Column(name = "service")
    private String serviceName;
    @Column(name = "price")
    private double price;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private MaintenanceCategory category;
    @Column(name = "available")
    private boolean isAvailable;
}

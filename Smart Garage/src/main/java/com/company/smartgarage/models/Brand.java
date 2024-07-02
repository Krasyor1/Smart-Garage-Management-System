package com.company.smartgarage.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private int id;
    @Column(name = "brand_name")
    private String brandName;
    @Column(name = "code")
    private String code;
}

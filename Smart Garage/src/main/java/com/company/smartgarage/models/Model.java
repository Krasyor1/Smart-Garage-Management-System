package com.company.smartgarage.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "models")
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "model_name")
    private String modelName;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
}

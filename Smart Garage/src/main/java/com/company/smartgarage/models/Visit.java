package com.company.smartgarage.models;

import com.company.smartgarage.models.enums.Currency;
import com.company.smartgarage.models.enums.VisitStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@Table(name = "visits")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "visit_date")
    private LocalDate creationDate;

    @Column(name = "total_price")
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "currency")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private VisitStatus status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "visit_id")
    private Set<DoneService> services;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Visit visit = (Visit) obj;
        return id == visit.id;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", totalPrice=" + totalPrice +
                ", currency=" + currency +
                ", status=" + status +
                '}';
    }
}

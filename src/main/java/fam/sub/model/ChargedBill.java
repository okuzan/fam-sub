package fam.sub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Month;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "charged_bill", uniqueConstraints = @UniqueConstraint(columnNames = {"service_id", "year", "month"}))
public class ChargedBill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Service service;

    private double amount;

    private Month month;
    private int year;

    public ChargedBill(Service service, double charged, Month month, int year) {
        this.service = service;
        this.amount = charged;
        this.month = month;
        this.year = year;
    }
}

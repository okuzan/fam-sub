package fam.sub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;

@Entity
@Getter
@NoArgsConstructor
public class ChargedBill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Service service;

    private double amount;

    private Month month;
    private int year;

    public ChargedBill(Service service, double charged, Month month) {
        this.service = service;
        this.amount = charged;
        this.month = month;
        this.year = LocalDate.now().getYear();
    }
}

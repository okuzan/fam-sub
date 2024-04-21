package fam.sub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "membership", uniqueConstraints = @UniqueConstraint(columnNames = {"person_id", "service_id", "year", "season"}))
public class SeasonalUserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    private double months;

    private Season season;
    private int year;
    private double amount;

    public SeasonalUserSubscription(SeasonalUserSubscription subscription) {
        this.person = subscription.getPerson();
        this.service = subscription.getService();
        this.months = subscription.getMonths();
        this.year = subscription.getYear();
        this.season = subscription.getSeason();
        this.amount = subscription.getAmount();
    }

    public SeasonalUserSubscription(Person person, Service service, double months, Season season, int year) {
        this.person = person;
        this.service = service;
        this.months = months;
        this.season = season;
        this.year = year;
    }
}

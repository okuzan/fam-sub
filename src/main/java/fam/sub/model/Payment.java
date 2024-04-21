package fam.sub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Person person;

    private LocalDateTime timestamp;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    public Payment(Person person, LocalDateTime now, int topUpAmount, String paymentMethod) {
        this.person = person;
        this.timestamp = now;
        this.amount = topUpAmount;
        this.paymentMethod = PaymentMethod.valueOf(paymentMethod);
    }

    public enum PaymentMethod {
        PRIVAT,
        MONO,
        REVOLUT
    }
}

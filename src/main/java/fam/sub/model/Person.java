package fam.sub.model;

import fam.sub.util.SeasonUtility;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private double balance;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    List<SeasonalUserSubscription> seasonalUserSubscriptions;

    public List<SeasonalUserSubscription> getCurrentSubscriptions() {
        return this.getSeasonalUserSubscriptions()
                .stream()
                .filter(sub -> sub.getSeason().equals(SeasonUtility.getCurrentSeason()))
                .toList();
    }
}

package fam.sub.repository;

import fam.sub.model.Person;
import fam.sub.model.Season;
import fam.sub.model.SeasonalUserSubscription;
import fam.sub.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SeasonalUserSubscription, Long> {
    List<SeasonalUserSubscription> findAllByPersonAndSeasonAndYear(Person person, Season season, int year);

    Optional<SeasonalUserSubscription> findByPersonAndServiceAndSeasonAndYear(Person person, Service service, Season season, int year);

    List<SeasonalUserSubscription> findAllByServiceAndSeasonAndYear(Service service, Season season, int year);
}

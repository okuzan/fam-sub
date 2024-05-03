package fam.sub.service;

import fam.sub.model.ChargedBill;
import fam.sub.model.Person;
import fam.sub.model.Season;
import fam.sub.model.SeasonDate;
import fam.sub.model.SeasonalUserSubscription;
import fam.sub.model.Service;
import fam.sub.repository.ChargedBillRepository;
import fam.sub.repository.PersonRepository;
import fam.sub.repository.SubscriptionRepository;
import fam.sub.util.SeasonUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static fam.sub.util.SeasonUtility.MONTHS_IN_SEASON;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BalanceService {
    private static final double PRECISION = 100.0;
    private final PersonRepository personRepository;
    private final ChargedBillRepository chargedBillRepository;
    private final SubscriptionRepository subscriptionRepository;

    @SneakyThrows
    public void updateBalances(Person person, boolean execute) {
        Season season = SeasonUtility.getCurrentSeason();
        int year = LocalDate.now().getYear();
        List<SeasonalUserSubscription> seasonalUserBills = subscriptionRepository.findAllByPersonAndSeasonAndYear(person, season, year);

        double totalCharges = Math.ceil(
                seasonalUserBills.stream()
                        .mapToDouble(SeasonalUserSubscription::getAmount)
                        .sum());

        double newBalance = person.getBalance() - totalCharges;
        System.out.println(person.getName() + " : " + (int) totalCharges);
        seasonalUserBills.forEach(subscription -> {
            System.out.println(subscription.getService().getName() + " : " + subscription.getAmount());
        });
        System.out.println("--------------------");

        if (execute) {
            person.setBalance(newBalance);
            personRepository.save(person);
        }
    }

    @Transactional
    public void calculateCurrentSeasonBillsForAllUsers() {
        List<Person> people = personRepository.findAll();
        for (Person person : people) {
            calculateSeasonUserBill(person, SeasonUtility.getCurrentSeasonDate());
        }
    }

    @Transactional
    public void showAndUpdateBalancesForAllUsers(boolean applyChanges) {
        List<Person> people = personRepository.findAll();
        for (Person person : people) {
            updateBalances(person, applyChanges);
        }
    }

    public double getServiceCostBySeason(Service service, SeasonDate seasonDate) {
        Set<Month> months = SeasonUtility.getMonthsOfSeason(seasonDate.getSeason());
        List<ChargedBill> chargedBills = chargedBillRepository.findAllByMonthInAndYear(months, seasonDate.getYear());
        return chargedBills.stream()
                .filter(chargedBill -> chargedBill.getService().equals(service))
                .mapToDouble(ChargedBill::getAmount)
                .sum();
    }

    @Transactional
    public void calculateSeasonUserBill(Person person, SeasonDate seasonDate) {
        List<SeasonalUserSubscription> seasonalUserSubscriptions = subscriptionRepository
                .findAllByPersonAndSeasonAndYear(person, seasonDate.getSeason(), seasonDate.getYear());

        for (SeasonalUserSubscription subscription : seasonalUserSubscriptions) {
            double seasonalServiceCharge = getServiceCostBySeason(subscription.getService(), seasonDate);

            List<SeasonalUserSubscription> allSeasonalServiceSubs = subscriptionRepository
                    .findAllByServiceAndSeasonAndYear(subscription.getService(), seasonDate.getSeason(), seasonDate.getYear());

            double totalMonths = allSeasonalServiceSubs.stream()
                    .mapToDouble(SeasonalUserSubscription::getMonths)
                    .sum();

            double share = subscription.getMonths() / totalMonths; // how much of the service the user used
            double amount = share * seasonalServiceCharge;
            amount = roundToCent(amount);

            subscription.setAmount(amount);
            System.out.println("User: " + person.getName() + " Service: " + subscription.getService().getName() + " Amount: " + amount);
            subscriptionRepository.save(subscription);
        }
    }


    @Transactional
    public double getNextSeasonBillForUser(Person person) {
        SeasonDate seasonDate = SeasonUtility.getNextSeasonDate();

        return subscriptionRepository
                .findAllByPersonAndSeasonAndYear(person, seasonDate.getSeason(), seasonDate.getYear())
                .stream()
                .mapToDouble(SeasonalUserSubscription::getAmount)
                .sum();
    }

    // Assuming that bills for the next season are already generated but with zero amount
    @Transactional
    public void calculateBillsForNextSeason() {
        Season nextSeason = SeasonUtility.getNextSeason();
        List<Person> people = personRepository.findAll();

        for (Person person : people) {
            for (SeasonalUserSubscription subscription : person.getCurrentSubscriptions()) {
                fam.sub.model.Service service = subscription.getService();
                double cost = Math.ceil(estimateNextSeasonServiceCostForUser(service));
                SeasonalUserSubscription futureSubscription =
                        subscriptionRepository
                                .findByPersonAndServiceAndSeasonAndYear(person,
                                        service,
                                        nextSeason,
                                        LocalDate.now().getYear()
                                )
                                .orElseThrow();
                futureSubscription.setAmount(cost);
                subscriptionRepository.save(futureSubscription);
            }
        }
    }

    @Transactional
    public void populateMembershipsForNextSeason() {
        List<Person> people = personRepository.findAll();
        Season nextSeason = SeasonUtility.getNextSeason();

        for (Person person : people) {
            for (SeasonalUserSubscription subscription : person.getCurrentSubscriptions()) {
                createSameSubscriptionForNextSeason(subscription, nextSeason);
            }
        }
    }

    private double estimateNextSeasonServiceCostForUser(fam.sub.model.Service service) {
        long currentMembers = subscriptionRepository.findAllByServiceAndSeasonAndYear(
                service,
                SeasonUtility.getCurrentSeason(),
                LocalDate.now().getYear()
        ).size();

        double futureTotalCost = getServiceCostBySeason(service, SeasonUtility.getCurrentSeasonDate());

        return roundToCent(futureTotalCost / currentMembers);
    }

    private void createSameSubscriptionForNextSeason(SeasonalUserSubscription subscription, Season nextSeason) {
        SeasonalUserSubscription newSubscription = new SeasonalUserSubscription(subscription);
        newSubscription.setMonths(MONTHS_IN_SEASON);
        newSubscription.setSeason(nextSeason);
        newSubscription.setAmount(0);
        subscriptionRepository.save(newSubscription);
    }

    private static double roundToCent(double amount) {
        return Math.round(amount * PRECISION) / PRECISION;
    }
}

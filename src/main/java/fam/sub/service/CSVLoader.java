package fam.sub.service;

import fam.sub.model.ChargedBill;
import fam.sub.model.Payment;
import fam.sub.model.Person;
import fam.sub.model.SeasonalUserSubscription;
import fam.sub.model.Service;
import fam.sub.repository.ChargedBillRepository;
import fam.sub.repository.PaymentRepository;
import fam.sub.repository.PersonRepository;
import fam.sub.repository.ServiceRepository;
import fam.sub.repository.SubscriptionRepository;
import fam.sub.util.SeasonUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Transactional
@Component
@RequiredArgsConstructor
public class CSVLoader {
    private static final String CSV_TEMPLATE = "src/main/resources/data/{}.csv";

    private static final String BALANCE_CSV = CSV_TEMPLATE.replace("{}", "balance");
    private static final String MEMBERSHIP_CSV = CSV_TEMPLATE.replace("{}", "membership");
    private static final String TOPUPS_CSV = CSV_TEMPLATE.replace("{}", "topups");
    private static final String CHARGED_CSV = CSV_TEMPLATE.replace("{}", "charged");

    private static final String SEPARATOR = ",";
    private static final int NAME_INDEX = 0;
    private static final int SUM_INDEX = 1;
    private static final int SERVICE_NAME_INDEX = 0;
    private static final int USER_NAME_INDEX = 1;
    private static final int MONTHS_INDEX = 2;
    private static final int FEE_INDEX = 1;
    public static final String SKIP_LINE_SYMBOL = "#";
    private static final int METHOD_INDEX = 3;


    private final PersonRepository personRepository;
    private final ServiceRepository serviceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final ChargedBillRepository chargedBillRepository;

    @SneakyThrows
    public void parseTopUps() {
        List<String> lines = Files.readAllLines(Paths.get(TOPUPS_CSV));
        for (String line : lines) {
            if (line.startsWith(SKIP_LINE_SYMBOL)) {
                continue;
            }

            String[] columns = line.split(SEPARATOR);
            String name = columns[NAME_INDEX];
            int topUpAmount = Integer.parseInt(columns[SUM_INDEX]);
            Person person = personRepository.findByName(name).orElseThrow();

            Payment payment = new Payment(person, LocalDateTime.now(), topUpAmount);
            paymentRepository.save(payment);

            person.setBalance(person.getBalance() + topUpAmount);
            personRepository.save(person);
        }
    }

    @SneakyThrows
    public void parseChargedBills() {
        List<String> lines = Files.readAllLines(Paths.get(CHARGED_CSV));
        for (String line : lines) {
            String[] columns = line.split(SEPARATOR);
            String serviceName = columns[SERVICE_NAME_INDEX];
            double charged = Double.parseDouble(columns[FEE_INDEX]);
            Month month = Month.of(Integer.parseInt(columns[MONTHS_INDEX]));
            Service service = serviceRepository.findByName(serviceName).orElseThrow();
            chargedBillRepository.save(new ChargedBill(service, charged, month));
        }
    }

    @SneakyThrows
    public void parseBalance() {
        List<String> lines = Files.readAllLines(Paths.get(BALANCE_CSV));
        for (String line : lines) {
            String[] columns = line.split(SEPARATOR);
            String name = columns[NAME_INDEX];
            int newBalance = Integer.parseInt(columns[SUM_INDEX]);

            Person person = personRepository.findByName(name).orElseThrow();
            person.setBalance(newBalance);
            personRepository.save(person);
        }
    }

    @SneakyThrows
    public void parseSubscriptions() {
        List<String> lines = Files.readAllLines(Path.of(MEMBERSHIP_CSV));
        for (String line : lines) {
            String[] columns = line.split(SEPARATOR);
            String serviceName = columns[SERVICE_NAME_INDEX];
            String userName = columns[USER_NAME_INDEX];
            double months = Double.parseDouble(columns[MONTHS_INDEX]);

            Person person = personRepository.findByName(userName).orElseThrow();
            Service service = serviceRepository.findByName(serviceName).orElseThrow();

            subscriptionRepository.save(new SeasonalUserSubscription(
                    person,
                    service,
                    months,
                    SeasonUtility.getCurrentSeason(),
                    LocalDateTime.now().getYear()
            ));
        }
    }
}

package fam.sub.service;

import fam.sub.model.ChargedBill;
import fam.sub.model.Payment;
import fam.sub.model.PaymentMethod;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Transactional
@Component
@RequiredArgsConstructor
public class CSVLoader {
    private static final String CSV_TEMPLATE = "src/main/resources/data/current/{}.csv";

    private static final String MEMBERSHIP_CSV = CSV_TEMPLATE.replace("{}", "membership");
    private static final String TOPUPS_CSV = CSV_TEMPLATE.replace("{}", "topups");
    private static final String CHARGED_CSV = CSV_TEMPLATE.replace("{}", "charged");

    private static final String SEPARATOR = ",";
    private static final int NAME_INDEX = 0;
    private static final int SUM_INDEX = 1;
    private static final int SERVICE_NAME_INDEX = 0;
    public static final String SKIP_LINE_SYMBOL = "#";

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
            PaymentMethod paymentMethod = PaymentMethod.getByCode(columns[2]);
            Payment payment = new Payment(person, LocalDateTime.now(), topUpAmount, paymentMethod);
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
            Service service = serviceRepository.findByName(serviceName).orElseThrow();

            for (int i = 1; i < columns.length; i++) {
                double charged = Double.parseDouble(columns[i]);
                if (charged == 0) {
                    continue;
                }
                Month month = i == 1 ? Month.DECEMBER : Month.of(i - 1);

                int year = i == 1 ? LocalDateTime.now().getYear() - 1 : LocalDateTime.now().getYear();
                chargedBillRepository.save(new ChargedBill(service, charged, month, year));
            }
        }
    }

    @SneakyThrows
    public void parseMemberships() {
        List<String> lines = Files.readAllLines(Path.of(MEMBERSHIP_CSV));
        for (String line : lines) {
            String[] columns = line.split(SEPARATOR);
            String serviceName = columns[SERVICE_NAME_INDEX];
            Service service = serviceRepository.findByName(serviceName).orElseThrow();

            for (int i = 1; i < columns.length; i++) {
                String userName = columns[i];
                Person person = personRepository.findByName(userName).orElseThrow();

                subscriptionRepository.save(new SeasonalUserSubscription(
                        person,
                        service,
                        3, // Assume months duration of 3 everywhere
                        SeasonUtility.getCurrentSeason(),
                        LocalDateTime.now().getYear()
                ));
            }
        }
    }
}

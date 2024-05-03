package fam.sub.service;

import fam.sub.model.Person;
import fam.sub.model.Season;
import fam.sub.repository.PersonRepository;
import fam.sub.util.SeasonUtility;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class PinnedPostService {
    public static final String PINNED_RESULT = "src/main/resources/output/pinned.md";
    public static final String PINNED_POST_TEMPLATE = "src/main/resources/templates/pinned.md";
    public static final String TIMESTAMP_PATTERN = "MMM dd HH:mm";
    public static final String LINE = "————————————————————————\n";
    private final PersonRepository personRepository;
    private final BalanceService balanceService;

    @Value("${users.excluded}")
    private String excludedUsersString;

    @Autowired
    public PinnedPostService(PersonRepository personRepository, BalanceService balanceService) {
        this.personRepository = personRepository;
        this.balanceService = balanceService;
    }

    @SneakyThrows(IOException.class)
    public void generatePinnedPost() {
        Season currentSeason = SeasonUtility.getCurrentSeason();
        List<Person> users = personRepository.findAllByOrderByBalance();

        String[] excludedUsers = excludedUsersString.split(",");
        users = users.stream()
                .filter(user -> Arrays.stream(excludedUsers).noneMatch(user.getName()::equals))
                .toList();

        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));

        String str = Files.readString(Paths.get(PINNED_POST_TEMPLATE));

        StringBuilder negativeBalances = new StringBuilder();
        StringBuilder zeroBalances = new StringBuilder();
        StringBuilder positiveBalances = new StringBuilder();

        for (Person user : users) {
            String balanceEmoji;
            String seasonEmoji;
            StringBuilder currentBalance;
            if (user.getBalance() < 0) {
                balanceEmoji = "➖";
                seasonEmoji = SeasonUtility.getPrevSeason().getEmoji();
                currentBalance = negativeBalances;
            } else if (user.getBalance() > 0) {
                balanceEmoji = "➕";
                currentBalance = positiveBalances;

                seasonEmoji = balanceService.getNextSeasonBillForUser(user) <= user.getBalance() ?
                        SeasonUtility.getNextSeason().getEmoji() :
                        SeasonUtility.getCurrentSeason().getEmoji();
            } else {
                balanceEmoji = "0️⃣";
                currentBalance = zeroBalances;
                seasonEmoji = SeasonUtility.getCurrentSeason().getEmoji();
            }
            currentBalance.append(balanceEmoji)
                    .append(" ");
            if (user.getBalance() != 0) {
                currentBalance.append("**")
                        .append(Math.abs((int) user.getBalance()))
                        .append("** ");
            }
            currentBalance.append("**")
                    .append(user.getName())
                    .append("** ")
                    .append(seasonEmoji)
                    .append("\n");
        }

        StringBuilder balances = new StringBuilder();
        if (!negativeBalances.isEmpty()) {
            balances.append(negativeBalances);
        }
        if (!zeroBalances.isEmpty()) {
            balances.append(LINE).append(zeroBalances);
        }
        if (!positiveBalances.isEmpty()) {
            balances.append(LINE).append(positiveBalances);
        }

        String result = String.format(str, currentDate,
                currentSeason.getEmoji(),
                currentSeason.name().toUpperCase(),
                balances
        );

        Files.write(Paths.get(PINNED_RESULT), result.getBytes());
    }

}
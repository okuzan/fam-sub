package fam.sub;

import fam.sub.service.BalanceService;
import fam.sub.service.CSVLoader;
import fam.sub.service.PinnedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class FamSubManager {
    public static void main(String[] args) {
        SpringApplication.run(FamSubManager.class, args);
    }

    @Bean
    public CommandLineRunner run(BalanceService balanceService,
                                 CSVLoader csvLoader,
                                 PinnedPostService pinnedPostService
    ) {
        return args -> {
        };
    }
}

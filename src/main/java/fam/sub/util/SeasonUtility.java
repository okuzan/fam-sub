package fam.sub.util;

import fam.sub.model.Season;
import fam.sub.model.SeasonDate;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class SeasonUtility {
    public static final int MONTHS_IN_SEASON = 3;

    private static final Map<Month, Season> monthToSeasonMap = Map.ofEntries(
            Map.entry(Month.MARCH, Season.SPRING),
            Map.entry(Month.APRIL, Season.SPRING),
            Map.entry(Month.MAY, Season.SPRING),
            Map.entry(Month.JUNE, Season.SUMMER),
            Map.entry(Month.JULY, Season.SUMMER),
            Map.entry(Month.AUGUST, Season.SUMMER),
            Map.entry(Month.SEPTEMBER, Season.AUTUMN),
            Map.entry(Month.OCTOBER, Season.AUTUMN),
            Map.entry(Month.NOVEMBER, Season.AUTUMN),
            Map.entry(Month.DECEMBER, Season.WINTER),
            Map.entry(Month.JANUARY, Season.WINTER),
            Map.entry(Month.FEBRUARY, Season.WINTER)
    );

    public Season getSeason(Month month) {
        return monthToSeasonMap.get(month);
    }

    public Set<Month> getMonthsOfSeason(Season season) {
        return monthToSeasonMap.entrySet().stream()
                .filter(entry -> entry.getValue() == season)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static Season getCurrentSeason() {
        return getSeason(Month.from(LocalDate.now()));
    }

    public static Season getNextSeason() {
        return getSeason(Month.from(LocalDate.now()).plus(MONTHS_IN_SEASON));
    }

    public static Season getPrevSeason() {
        return getSeason(Month.from(LocalDate.now()).minus(MONTHS_IN_SEASON));
    }

    public static SeasonDate getCurrentSeasonDate() {
        return new SeasonDate(getCurrentSeason(), LocalDate.now().getYear());
    }

    public static SeasonDate getNextSeasonDate(SeasonDate seasonDate) {
        int currentYear = seasonDate.getYear();
        Season currentSeason = seasonDate.getSeason();
        int nextYear = Season.AUTUMN == currentSeason ? currentYear + 1 : currentYear;
        return new SeasonDate(getNextSeason(currentSeason), nextYear);
    }

    public static SeasonDate getNextSeasonDate() {
        int currentYear = LocalDate.now().getYear();
        Season currentSeason = getCurrentSeason();
        int nextYear = Season.AUTUMN == currentSeason ? currentYear + 1 : currentYear;
        return new SeasonDate(getNextSeason(currentSeason), nextYear);
    }

    // get season before current season
    public static Season getPreviousSeason(Season season) {
        Season[] seasons = Season.values();
        return seasons[(season.ordinal() - 1 + seasons.length) % seasons.length];
    }

    // get season after current season
    public static Season getNextSeason(Season season) {
        Season[] seasons = Season.values();
        return seasons[(season.ordinal() + 1) % seasons.length];
    }
}

package fam.sub.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeasonDate {
    private Season season;
    private int year; // TODO change to Year
}

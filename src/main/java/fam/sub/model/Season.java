package fam.sub.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Season {
    WINTER("☃️"),
    SPRING("🌿"),
    SUMMER("🏖️"),
    AUTUMN("🍁");

    private final String emoji;
}

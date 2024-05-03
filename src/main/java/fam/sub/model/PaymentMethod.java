package fam.sub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentMethod {
    REVOLUT("r"),
    PRIVAT("p"),
    MONOBANK("m");

    private final String code;

    public static PaymentMethod getByCode(String code) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.code.equals(code)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("No such payment method with code: " + code);
    }
}

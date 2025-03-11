package com.github.llcrystal.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Payment {
    public static BigDecimal originAmount = new BigDecimal(20);

    private String id;
    private BigDecimal totalAmount;

    public static Payment doPayment(BigDecimal amount) {
        originAmount = originAmount.subtract(amount);

        Payment payment = new Payment();
        payment.setId("1");
        payment.setTotalAmount(amount);
        return payment;
    }
}

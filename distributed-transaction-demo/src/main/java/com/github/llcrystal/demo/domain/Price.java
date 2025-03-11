package com.github.llcrystal.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Price {
    private static final String PRODUCT_ID = "product_test_id";

    private String productId;
    private BigDecimal productPrice;

    public static Price queryPrice(String productId) {
        if (!PRODUCT_ID.equals(productId)) {
            throw new RuntimeException("Price not exist!");
        }
        Price price = new Price();
        price.setProductId(productId);
        price.setProductPrice(new BigDecimal(5));
        return price;
    }
}

package com.github.llcrystal.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Inventory {
    private static final String PRODUCT_ID = "product_test_id";

    public static BigDecimal originQuantity = BigDecimal.TEN;

    private String productId;
    private BigDecimal inventoryQuantity;

    public static Inventory reduceInventory(String productId, BigDecimal quantity) {
        if (!PRODUCT_ID.equals(productId)) {
            throw new RuntimeException("Inventory not exist!");
        }
        originQuantity = originQuantity.subtract(quantity);

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setInventoryQuantity(originQuantity.subtract(quantity));
        return inventory;
    }
}

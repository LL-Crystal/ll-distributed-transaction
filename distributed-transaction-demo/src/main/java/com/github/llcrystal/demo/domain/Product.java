package com.github.llcrystal.demo.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Product {
    private static final String PRODUCT_ID = "product_test_id";

    private String id;
    private String name;

    public static Product queryProduct(String productId) {
        if (!PRODUCT_ID.equals(productId)) {
            throw new RuntimeException("Product not exist!");
        }
        Product product = new Product();
        product.setId(productId);
        product.setName("name");
        return product;
    }
}

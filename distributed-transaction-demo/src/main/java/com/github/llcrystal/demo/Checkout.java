package com.github.llcrystal.demo;

import com.github.llcrystal.demo.domain.*;
import com.github.llcrystal.transaction.Idempotent;
import com.github.llcrystal.transaction.domain.aggregate.transaction.valueobj.IdempotentKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Setter
@Getter
@Component
public class Checkout {
    private Product product;
    private Price price;
    private Order order;
    private Inventory inventory;
    private Payment payment;

    public void queryProduct(String productId) {
        this.product = Product.queryProduct(productId);
    }

    public void queryPrice(String productId) {
        this.price = Price.queryPrice(productId);
    }

    @Idempotent(invoke = "IDEMPOTENT_STEP1")
    public void createOrder(IdempotentKey key, String orderId, BigDecimal productQuantity, int time) {
        this.order = Order.createOrder(orderId, this.product, this.price, productQuantity);
    }

    @Idempotent(invoke = "IDEMPOTENT_STEP2")
    public void reduceInventory(IdempotentKey key, BigDecimal quantity, int time) {
        if (time == 1) {
            log.info("Reduce inventory error!");
            throw new RuntimeException("Reduce inventory error!");
        }
        this.inventory = Inventory.reduceInventory(this.product.getId(), quantity);
    }

    @Idempotent(invoke = "IDEMPOTENT_STEP3")
    public void doPayment(IdempotentKey key, int time) {
        if (time == 2) {
            log.info("Do payment error!");
            throw new RuntimeException("Do payment error!");
        }
        this.payment = Payment.doPayment(this.order.getTotalAmount());
    }
}

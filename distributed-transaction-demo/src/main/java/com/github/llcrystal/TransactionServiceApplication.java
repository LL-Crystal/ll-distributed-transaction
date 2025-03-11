package com.github.llcrystal;

import com.github.llcrystal.demo.Checkout;
import com.github.llcrystal.demo.domain.Inventory;
import com.github.llcrystal.demo.domain.Payment;
import com.github.llcrystal.transaction.EnableDistributedTransaction;
import com.github.llcrystal.transaction.domain.aggregate.transaction.valueobj.IdempotentKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@EnableDistributedTransaction
@ConfigurationPropertiesScan
@SpringBootApplication
public class TransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }

    @Component
    public static class TransactionTestRunner implements CommandLineRunner {

        @Autowired
        private Checkout checkout;

        @Override
        public void run(String... args) throws Exception {
            String orderId = UUID.randomUUID().toString();
            IdempotentKey key = new IdempotentKey("user", orderId, "MY_TRANSACTION_1");

            log.info("The origin inventory is {}, the origin amount is {}", Inventory.originQuantity, Payment.originAmount);
            for (int time = 1; time <= 3; time++) {
                try {
                    checkout.queryProduct("product_test_id");
                    checkout.queryPrice("product_test_id");
                    checkout.createOrder(key, orderId, new BigDecimal(3), time);
                    checkout.reduceInventory(key, new BigDecimal(3), time);
                    checkout.doPayment(key, time);
                } catch (Exception e) {
                    log.warn("Error:", e);
                }
            }
            log.info("The current inventory is {}, the current amount is {}", Inventory.originQuantity, Payment.originAmount);
        }
    }
}

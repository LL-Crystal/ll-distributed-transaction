package com.github.llcrystal.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Order {
    private String orderId;
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal totalAmount;

    public static Order createOrder(String orderId, Product product, Price price, BigDecimal productQuantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductQuantity(productQuantity);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        Order order = new Order();
        order.setOrderId(orderId);
        order.setItems(orderItems);
        order.setTotalAmount(price.getProductPrice().multiply(productQuantity));
        return order;
    }

    @Setter
    @Getter
    public static class OrderItem {
        private String productId;
        private String productName;
        private BigDecimal productQuantity;
    }
}

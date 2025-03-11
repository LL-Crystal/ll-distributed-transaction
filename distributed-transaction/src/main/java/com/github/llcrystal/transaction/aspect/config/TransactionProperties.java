package com.github.llcrystal.transaction.aspect.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "ll")
public class TransactionProperties {
    private Map<String, List<String>> transaction;

    public void setTransaction(Map<String, List<String>> transaction) {
        this.transaction = transaction;
    }
}

package com.github.llcrystal;

import com.github.llcrystal.transaction.aspect.config.TransactionProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {"com.github.llcrystal.transaction"})
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionConfiguration {
}

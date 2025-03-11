package com.github.llcrystal.transaction;

import com.github.llcrystal.TransactionConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableIDistributedTransaction
 *     Define a transaction consisting of multi-step calls.
 *     Use @Idempotent to ensure idempotent call of one step.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionConfiguration.class)
public @interface EnableDistributedTransaction {

}

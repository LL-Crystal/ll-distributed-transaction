package com.github.llcrystal.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Idempotent
 *   If any of the invoke history contain the required result,
 *   the result is returned and the method is not invoked.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Idempotent {

    /**
     * Idempotent invoke.
     */
    String invoke();
}

package com.github.llcrystal.transaction.infrastructure.cache;

import com.github.llcrystal.transaction.domain.aggregate.transaction.entity.IdempotentEntity;

public class IdempotentRecordCache {
    public static final Integer MAX_SIZ = 5000;

    private static final ThreadSafeMap<String, IdempotentEntity> IDEMPOTENT_RECORD_MAP = new ThreadSafeMap<>(MAX_SIZ);

    private IdempotentRecordCache() {
        throw new IllegalStateException("Utility class");
    }

    public static ThreadSafeMap<String, IdempotentEntity> getIdempotentRecords() {
        return IDEMPOTENT_RECORD_MAP;
    }
}

package com.github.llcrystal.transaction.domain.external.port;

import com.github.llcrystal.transaction.domain.aggregate.transaction.entity.IdempotentEntity;

import java.util.Optional;

public interface IdempotentPort {

    Optional<IdempotentEntity> findByIdempotentKey(String idempotentKey);

    void save(IdempotentEntity entity);

    void update(IdempotentEntity entity);

    void clearIdempotentRecords(String transactionName);
}

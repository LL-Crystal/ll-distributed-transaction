package com.github.llcrystal.transaction.domain.aggregate.transaction.service;

import com.github.llcrystal.transaction.domain.aggregate.transaction.entity.IdempotentEntity;
import com.github.llcrystal.transaction.domain.external.port.IdempotentPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class IdempotentService {
    private final IdempotentPort idempotentPort;

    public Optional<IdempotentEntity> findByIdempotentKey(String idempotentKey) {
        return idempotentPort.findByIdempotentKey(idempotentKey);
    }

    public void save(IdempotentEntity entity) {
        idempotentPort.save(entity);
    }

    public void update(IdempotentEntity entity) {
        idempotentPort.update(entity);
    }

    public void clearIdempotentRecords(String transactionName) {
        idempotentPort.clearIdempotentRecords(transactionName);
    }
}

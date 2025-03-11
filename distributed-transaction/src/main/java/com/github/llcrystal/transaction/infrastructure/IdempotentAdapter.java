package com.github.llcrystal.transaction.infrastructure;

import com.github.llcrystal.transaction.domain.TransactionException;
import com.github.llcrystal.transaction.domain.aggregate.transaction.entity.IdempotentEntity;
import com.github.llcrystal.transaction.domain.external.port.IdempotentPort;
import com.github.llcrystal.transaction.infrastructure.cache.IdempotentRecordCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdempotentAdapter implements IdempotentPort {

    @Override
    public Optional<IdempotentEntity> findByIdempotentKey(String idempotentKey) {
        return Optional.ofNullable(IdempotentRecordCache.getIdempotentRecords().get(idempotentKey));
    }

    @Override
    public void save(IdempotentEntity entity) {
        if (IdempotentRecordCache.getIdempotentRecords().containsKey(entity.getIdempotentKey())) {
            log.warn("IdempotentKey key duplication: {}", entity.getIdempotentKey());
            throw new TransactionException("IdempotentKey key duplication");
        }
        IdempotentRecordCache.getIdempotentRecords().put(entity.getIdempotentKey(), entity);
    }

    @Override
    public void update(IdempotentEntity entity) {
        IdempotentRecordCache.getIdempotentRecords().put(entity.getIdempotentKey(), entity);
    }

    @Override
    public void clearIdempotentRecords(String transactionName) {
        List<String> removeKeys = new ArrayList<>();
        for (Map.Entry<String, IdempotentEntity> entry : IdempotentRecordCache.getIdempotentRecords().getMap().entrySet()) {
            if (transactionName.equals(entry.getValue().getTransactionName())) {
                removeKeys.add(entry.getKey());
            }
        }
        removeKeys.forEach(key -> IdempotentRecordCache.getIdempotentRecords().remove(key));
    }
}

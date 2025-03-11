package com.github.llcrystal.transaction.domain.aggregate.transaction;

import com.github.llcrystal.transaction.Idempotent;
import com.github.llcrystal.transaction.aspect.config.TransactionProperties;
import com.github.llcrystal.transaction.domain.TransactionException;
import com.github.llcrystal.transaction.domain.aggregate.transaction.entity.IdempotentEntity;
import com.github.llcrystal.transaction.domain.aggregate.transaction.valueobj.IdempotentKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class TransactionItem {
    private final IdempotentKey idempotentKey;

    private IdempotentEntity idempotentRecord;
    private IdempotentEntity invokeHistory;

    public static IdempotentKey parseIdempotentKey(Object[] params) {
        if (params == null || params.length == 0) {
            log.warn("Transaction parseIdempotentKey error: The type of the first parameter must be forcibly IdempotentKey.");
            throw new TransactionException("Parameter type error!");
        }

        if (!(params[0] instanceof IdempotentKey key)) {
            log.warn("Transaction parseIdempotentKey error: The type of the first parameter must be forcibly IdempotentKey.");
            throw new TransactionException("Parameter type error!");
        }
        return key;
    }

    public TransactionItem(IdempotentKey idempotentKey) {
        this.idempotentKey = idempotentKey;
    }

    public String getTransactionName() {
        return this.idempotentKey.getTransactionName();
    }

    public void validateInvoke(TransactionProperties transactionProperties, String invokeName) {
        Map<String, List<String>> transaction = transactionProperties.getTransaction();
        List<String> invokeList = transaction.get(this.getTransactionName());
        if (invokeList == null || !invokeList.contains(invokeName)) {
            log.warn("Transaction validate error: Invoke {} not belong to transaction {}.", invokeName, this.getTransactionName());
            throw new TransactionException("Invoke not belong to transaction!");
        }
    }

    public IdempotentEntity createSuccessEntity(Idempotent annotation, String response) {
        this.idempotentRecord = IdempotentEntity.createSuccessEntity(this.idempotentKey, annotation, response);
        return this.idempotentRecord;
    }

    public IdempotentEntity createFailedEntity(Idempotent annotation, String response) {
        this.idempotentRecord = IdempotentEntity.createFailedEntity(this.idempotentKey, annotation, response);
        return this.idempotentRecord;
    }

    public void updateInvokeHistory(boolean invokeResult, String response, IdempotentEntity invokeHistory) {
        if (invokeHistory.isInvokeSuccess()) {
            return;
        }

        this.invokeHistory = invokeHistory;
        if (invokeResult) {
            this.invokeHistory.updateSuccessResponse(response);
        } else {
            this.invokeHistory.updateFailedResponse(response);
        }
    }

    public boolean isLastStep(String stepName, TransactionProperties transactionProperties) {
        Map<String, List<String>> transaction = transactionProperties.getTransaction();
        List<String> stepList = transaction.get(this.getTransactionName());
        return stepName.equals(stepList.get(stepList.size() - 1));
    }
}

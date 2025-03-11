package com.github.llcrystal.transaction.domain.aggregate.transaction.entity;

import com.github.llcrystal.transaction.Idempotent;
import com.github.llcrystal.transaction.domain.aggregate.transaction.valueobj.IdempotentKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class IdempotentEntity {
    private static final boolean INVOKE_SUCCESS = true;
    private static final boolean INVOKE_FAILED = false;

    private Integer id;

    private String idempotentKey;

    private String transactionName;

    private String invokeName;

    private String invokeResponse;

    private boolean invokeResult;

    private boolean isDel;

    private Date createdAt;

    private Date updatedAt;

    public static IdempotentEntity createSuccessEntity(IdempotentKey idempotentKey, Idempotent annotation, String response) {
        IdempotentEntity entity = new IdempotentEntity();
        entity.setIdempotentKey(idempotentKey.getIdempotentKey(annotation.invoke()));
        entity.setTransactionName(idempotentKey.getTransactionName());
        entity.setInvokeName(annotation.invoke());
        entity.setInvokeResponse(response);
        entity.setInvokeResult(INVOKE_SUCCESS);
        entity.setDel(false);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        return entity;
    }

    public static IdempotentEntity createFailedEntity(IdempotentKey idempotentKey, Idempotent annotation, String response) {
        IdempotentEntity entity = new IdempotentEntity();
        entity.setIdempotentKey(idempotentKey.getIdempotentKey(annotation.invoke()));
        entity.setTransactionName(idempotentKey.getTransactionName());
        entity.setInvokeName(annotation.invoke());
        entity.setInvokeResponse(response);
        entity.setInvokeResult(INVOKE_FAILED);
        entity.setDel(false);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        return entity;
    }

    public void updateSuccessResponse(String response) {
        this.setInvokeResponse(response);
        this.setInvokeResult(INVOKE_SUCCESS);
    }

    public void updateFailedResponse(String response) {
        this.setInvokeResponse(response);
        this.setInvokeResult(INVOKE_FAILED);
    }

    public boolean isInvokeSuccess() {
        return INVOKE_SUCCESS == this.isInvokeResult();
    }
}

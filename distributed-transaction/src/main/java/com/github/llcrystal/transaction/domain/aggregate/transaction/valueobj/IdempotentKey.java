package com.github.llcrystal.transaction.domain.aggregate.transaction.valueobj;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdempotentKey {

    private String identity;

    private String uniqueKey;

    private String transactionName;

    private String operate;

    public IdempotentKey(String identity, String uniqueKey, String transactionName) {
        this.identity = identity;
        this.uniqueKey = uniqueKey;
        this.transactionName = transactionName;
    }

    public IdempotentKey(String identity, String uniqueKey, String transactionName, String operate) {
        this.identity = identity;
        this.uniqueKey = uniqueKey;
        this.transactionName = transactionName;
        this.operate = operate;
    }

    public String getIdempotentKey(String invokeName) {
        String result = identity + "-" + uniqueKey + "-" + invokeName;
        if (operate == null) {
            return result;
        }
        return result + "-" + operate;
    }
}

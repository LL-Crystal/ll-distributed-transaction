package com.github.llcrystal.transaction.domain;

import lombok.Getter;

@Getter
public class TransactionException extends RuntimeException{

    public TransactionException(String msg) {
        super(msg);
    }

    public TransactionException(Throwable e) {
        super(e);
    }
}

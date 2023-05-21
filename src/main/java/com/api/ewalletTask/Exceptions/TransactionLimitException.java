package com.api.ewalletTask.Exceptions;

public class TransactionLimitException  extends  RuntimeException{
    public TransactionLimitException(String message) {
        super(message);
    }
}

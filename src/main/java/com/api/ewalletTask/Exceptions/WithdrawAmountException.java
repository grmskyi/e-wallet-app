package com.api.ewalletTask.Exceptions;

public class WithdrawAmountException extends RuntimeException{
    public WithdrawAmountException(String message) {
        super(message);
    }
}

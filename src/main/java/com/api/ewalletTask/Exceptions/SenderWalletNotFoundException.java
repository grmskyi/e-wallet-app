package com.api.ewalletTask.Exceptions;

public class SenderWalletNotFoundException extends RuntimeException{
    public SenderWalletNotFoundException(String message) {
        super(message);
    }
}

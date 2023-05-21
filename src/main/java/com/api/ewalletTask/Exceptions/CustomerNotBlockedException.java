package com.api.ewalletTask.Exceptions;

public class CustomerNotBlockedException extends RuntimeException{
    public CustomerNotBlockedException(String message) {
        super(message);
    }
}

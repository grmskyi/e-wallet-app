package com.api.ewalletTask.Exceptions;

public class CustomerBlockedException extends RuntimeException{
    public CustomerBlockedException(String message) {
        super(message);
    }
}

package com.api.ewalletTask.Exceptions;

public class SenderNotFoundException  extends  RuntimeException{
    public SenderNotFoundException(String message) {
        super(message);
    }
}

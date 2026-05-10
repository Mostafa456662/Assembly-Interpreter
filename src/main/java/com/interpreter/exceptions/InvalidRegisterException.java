package com.interpreter.exceptions;

public class InvalidRegisterException extends RuntimeException {
    public InvalidRegisterException(String message) {
        super(message);
    }
}

package com.interpreter.exceptions;

public class InvalidMemoryAddressException extends RuntimeException {
    public InvalidMemoryAddressException(String message) {
        super(message);
    }
}

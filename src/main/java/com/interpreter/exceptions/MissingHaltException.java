package com.interpreter.exceptions;

public class MissingHaltException extends RuntimeException {
    public MissingHaltException(String message) {
        super(message);
    }
}

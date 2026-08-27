package com.example.library_borrowing.exception;

public class ConflictException extends RuntimeException {
    public ConflictException (String message) {
        super(message);
    }
}

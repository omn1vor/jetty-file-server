package org.example.exception;

public class WrongFileSentException extends FileServiceException {
    public WrongFileSentException(String message) {
        super(message);
    }
}

package org.example.exception;

import lombok.Getter;

@Getter
public class FileServiceException extends RuntimeException {
    public FileServiceException(String message) {
        super(message);
    }
}

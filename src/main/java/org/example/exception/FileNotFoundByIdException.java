package org.example.exception;

public class FileNotFoundByIdException extends FileServiceException {
    public FileNotFoundByIdException(String id) {
        super(String.format("File with ID %s not found", id));
    }
}

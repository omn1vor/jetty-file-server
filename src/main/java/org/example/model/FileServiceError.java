package org.example.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class FileServiceError {
    int status;
    String error;
    LocalDateTime timestamp;

    public FileServiceError(int status, String error) {
        this.status = status;
        this.error = error;
        timestamp = LocalDateTime.now();
    }
}

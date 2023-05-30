package org.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter @Setter
public class FileServiceErrorInfo {
    private int status;
    private String error;
    private Instant timestamp = Instant.now();

    public FileServiceErrorInfo(int status, String error) {
        this.status = status;
        this.error = error;
    }
}

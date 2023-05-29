package org.example.service;

import org.example.model.FileInfo;

import java.io.InputStream;
import java.util.List;

public interface FileService {
    FileInfo uploadFile(InputStream inputStream, String fileName);

    InputStream downloadFile(String fileId);

    List<FileInfo> getFiles();
}

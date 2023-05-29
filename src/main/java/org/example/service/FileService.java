package org.example.service;

import org.example.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {
    FileInfo uploadFile(InputStream inputStream, String fileName) throws IOException;

    File getFile(String fileId);

    List<FileInfo> getFiles();
}

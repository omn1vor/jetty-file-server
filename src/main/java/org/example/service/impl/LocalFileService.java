package org.example.service.impl;

import org.example.model.FileInfo;
import org.example.service.FileService;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocalFileService implements FileService {
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public FileInfo uploadFile(InputStream inputStream, String fileName) throws IOException {
        String fileId = UUID.randomUUID().toString();

        File idDirectory = new File(UPLOAD_DIR, fileId);
        if (!idDirectory.exists()) {
            if (!idDirectory.mkdirs()) {
                throw new IOException("Could not write file due to internal error");
            }
        }

        File file = new File(idDirectory, fileName);
        Files.copy(inputStream, file.toPath());

        return new FileInfo(fileId, file.length() / 1024, fileName);
    }

    @Override
    public File getFile(String fileId) {
        return getFileFromIdDirectory(getIdDirectory(fileId));
    }

    @Override
    public List<FileInfo> getFiles() {
        File uploadDir = new File(UPLOAD_DIR);

        if (!uploadDir.exists() || !uploadDir.isDirectory()) {
            return List.of();
        }

        File[] idDirectories = uploadDir.listFiles();

        if (idDirectories == null) {
            return List.of();
        }

        List<FileInfo> fileList = new ArrayList<>();

        for (File idDirectory : idDirectories) {
            File file = getFileFromIdDirectory(idDirectory);
            fileList.add(new FileInfo(idDirectory.getName(), file.length() / 1024, file.getName()));
        }
        return fileList;
    }

    private File getFileFromIdDirectory(File idDirectory) {
        File[] files = idDirectory.listFiles();

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("File not found: " + idDirectory.getName());
        }

        return files[0];
    }

    private File getIdDirectory(String fileId) {
        File directory = new File(UPLOAD_DIR, fileId);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("File not found: " + fileId);
        }

        return directory;
    }
}

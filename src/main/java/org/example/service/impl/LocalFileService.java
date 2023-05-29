package org.example.service.impl;

import org.example.model.FileInfo;
import org.example.service.FileService;

import java.io.*;
import java.util.*;

public class LocalFileService implements FileService {
    private static final String UPLOAD_DIR = "uploads";
    private static final String FILE_MAPPING_FILE  = "file_mapping.txt";
    private final Map<String, String> fileMap;

    public LocalFileService() {
        this.fileMap = loadFileMap();
    }

    @Override
    public FileInfo uploadFile(InputStream inputStream, String fileName) {
        String fileId = UUID.randomUUID().toString();

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        File file = new File(uploadDir, fileId);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileMap.put(fileId, fileName);
        saveFileMap();

        return new FileInfo(fileId, file.length() / 1024, fileName);
    }

    @Override
    public InputStream downloadFile(String fileId) {
        File file = new File(UPLOAD_DIR, fileId);

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found: " + fileId);
        }
    }

    @Override
    public List<FileInfo> getFiles() {
        File uploadDir = new File(UPLOAD_DIR);

        if (!uploadDir.exists() || !uploadDir.isDirectory()) {
            return List.of();
        }

        File[] files = uploadDir.listFiles();
        List<FileInfo> fileList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                String id = file.getName();
                String filename = fileMap.getOrDefault(file.getName(), "filename_lost_index_failure");
                fileList.add(new FileInfo(id, file.length() / 1024, filename));
            }
        }
        return fileList;
    }

    private Map<String, String> loadFileMap() {
        File file = new File(FILE_MAPPING_FILE);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void saveFileMap() {
        try (FileOutputStream fos = new FileOutputStream(FILE_MAPPING_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(fileMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

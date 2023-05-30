package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.example.exception.FileServiceException;
import org.example.exception.WrongFileSentException;
import org.example.model.FileInfo;
import org.example.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.util.Collection;

@MultipartConfig(location = "target/multipart-tmp",
        fileSizeThreshold =  1024,
        maxFileSize = 1024 * 100,
        maxRequestSize = 1024 * 100 * 5)
public class FileUploadServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        Collection<Part> parts;

        try {
            parts = req.getParts();
        } catch (Exception e) {
            throw new WrongFileSentException(e.getMessage());
        }

        if (parts.size() == 0) {
            throw new WrongFileSentException("No files sent");
        }

        if (parts.size() > 1) {
            throw new WrongFileSentException("Only one file per form is supported");
        }

        FileService fileService = (FileService) getServletContext().getAttribute("fileService");

        Part part = parts.iterator().next();
        String fileName = part.getSubmittedFileName();

        if (!fileName.matches(".+\\.(txt|csv)$")) {
            throw new WrongFileSentException("Only txt or csv files are supported");
        }

        FileInfo fileInfo;
        try (InputStream inputStream = part.getInputStream()) {
            fileInfo = fileService.uploadFile(inputStream, fileName);
        } catch (IOException e) {
            throw new FileServiceException("Error while trying saving the file" + e.getMessage());
        }

        Gson gson = new GsonBuilder().create();

        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().print(gson.toJson(fileInfo));
    }
}

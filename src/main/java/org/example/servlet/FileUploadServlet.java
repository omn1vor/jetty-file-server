package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Collection<Part> parts = null;

        try {
            parts = req.getParts();
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().println(e.getMessage());
            return;
        }

        if (parts.size() == 0) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().println("No files sent");
            return;
        }

        if (parts.size() > 1) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().println("Only one file per form is supported");
            return;
        }

        FileService fileService = (FileService) getServletContext().getAttribute("fileService");

        Part part = parts.iterator().next();
        String fileName = part.getSubmittedFileName();

        FileInfo fileInfo;
        try (InputStream inputStream = part.getInputStream()) {
            fileInfo = fileService.uploadFile(inputStream, fileName);
        } catch (IOException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().println("Error while trying saving the file" + e.getMessage());
            return;
        }

        Gson gson = new GsonBuilder().create();

        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().print(gson.toJson(fileInfo));
    }
}

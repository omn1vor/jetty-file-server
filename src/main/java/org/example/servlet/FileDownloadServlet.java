package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.FileService;

import java.io.*;

public class FileDownloadServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        FileService fileService = (FileService) getServletContext().getAttribute("fileService");
        String fileId = (String) req.getAttribute("fileId");

        File file = fileService.getFile(fileId);

        try (InputStream fileStream = new FileInputStream(file)) {
            res.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            res.setContentType("application/octet-stream");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                res.getOutputStream().write(buffer, 0, bytesRead);
            }

            res.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

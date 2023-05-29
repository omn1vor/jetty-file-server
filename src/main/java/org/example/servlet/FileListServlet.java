package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.FileInfo;
import org.example.service.FileService;

import java.io.IOException;
import java.util.List;

public class FileListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        FileService fileService = (FileService) getServletContext().getAttribute("fileService");

        List<FileInfo> files = fileService.getFiles();
        Gson gson = new GsonBuilder().create();

        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().print(gson.toJson(files));
    }
}

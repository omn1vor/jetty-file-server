package org.example;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.filters.FileIdFilter;
import org.example.service.FileService;
import org.example.service.impl.LocalFileService;
import org.example.servlet.FileDownloadServlet;
import org.example.servlet.FileListServlet;
import org.example.servlet.FileUploadServlet;

import java.io.File;
import java.io.IOException;

public class JettyFileServerApp {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");

        String webappDir = "src/main/webapp";
        contextHandler.setResourceBase(webappDir);
        contextHandler.addServlet(DefaultServlet.class, "/");

        contextHandler.addFilter(FileIdFilter.class, "/download/*", null);

        contextHandler.addServlet(addMultipartConfig(FileUploadServlet.class), "/upload");
        contextHandler.addServlet(FileDownloadServlet.class, "/download/*");
        contextHandler.addServlet(FileListServlet.class, "/files");

        FileService fileService = new LocalFileService();
        contextHandler.setAttribute("fileService", fileService);

        server.setHandler(contextHandler);

        server.start();
        server.join();
    }

    private static ServletHolder addMultipartConfig(Class<? extends HttpServlet> servletClass) {
        ServletHolder servletHolder = new ServletHolder(new FileUploadServlet());
        MultipartConfigElement multipartConfig = new MultipartConfigElement(
                servletClass.getAnnotation(MultipartConfig.class));

        File tempDir = new File(multipartConfig.getLocation());
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        servletHolder.getRegistration().setMultipartConfig(multipartConfig);
        return servletHolder;
    }
}

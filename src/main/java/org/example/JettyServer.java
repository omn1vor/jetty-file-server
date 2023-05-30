package org.example;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.exception.FileServiceException;
import org.example.filter.ExceptionFilter;
import org.example.filter.FileIdFilter;
import org.example.service.FileService;
import org.example.service.impl.LocalFileService;
import org.example.servlet.FileDownloadServlet;
import org.example.servlet.FileListServlet;
import org.example.servlet.FileUploadServlet;

import java.io.File;
import java.net.URI;

public class JettyServer {

    public URI baseUri;

    public void start() throws Exception {
        Server server = new Server(8080);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");

        String webappDir = "src/main/webapp";
        contextHandler.setResourceBase(webappDir);
        contextHandler.addServlet(DefaultServlet.class, "/");

        contextHandler.addFilter(ExceptionFilter.class, "/*", null);
        contextHandler.addFilter(FileIdFilter.class, "/download/*", null);

        contextHandler.addServlet(addMultipartConfig(FileUploadServlet.class), "/upload");
        contextHandler.addServlet(FileDownloadServlet.class, "/download/*");
        contextHandler.addServlet(FileListServlet.class, "/files");

        FileService fileService = new LocalFileService();
        contextHandler.setAttribute("fileService", fileService);

        server.setHandler(contextHandler);

        server.start();

        baseUri = server.getURI();
    }

    @SuppressWarnings("SameParameterValue")
    private static ServletHolder addMultipartConfig(Class<? extends HttpServlet> servletClass) {
        ServletHolder servletHolder = new ServletHolder(new FileUploadServlet());
        MultipartConfigElement multipartConfig = new MultipartConfigElement(
                servletClass.getAnnotation(MultipartConfig.class));

        File tempDir = new File(multipartConfig.getLocation());
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new FileServiceException("Failed to initialize file storage");
        }

        servletHolder.getRegistration().setMultipartConfig(multipartConfig);
        return servletHolder;
    }
}

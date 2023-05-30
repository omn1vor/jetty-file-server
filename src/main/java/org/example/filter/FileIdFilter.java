package org.example.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FileIdFilter implements Filter {
    private static final String ID_PATTERN = "[a-zA-Z0-9-]+";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || !pathInfo.matches("/" + ID_PATTERN)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String fileId = pathInfo.substring(1);
        req.setAttribute("fileId", fileId);
        chain.doFilter(request, response);
    }
}

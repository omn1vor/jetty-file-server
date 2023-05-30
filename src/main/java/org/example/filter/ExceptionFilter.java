package org.example.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.FileNotFoundByIdException;
import org.example.exception.WrongFileSentException;
import org.example.model.FileServiceErrorInfo;

import java.io.IOException;
import java.time.Instant;

public class ExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            handleException(e, (HttpServletResponse) res);
        }
    }

    private void handleException(Exception e, HttpServletResponse res) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();

        res.setContentType("application/json");
        res.setStatus(getResponseStatus(e));
        FileServiceErrorInfo errorInfo = new FileServiceErrorInfo(res.getStatus(), e.getMessage());
        res.getWriter().write(gson.toJson(errorInfo));
    }

    private int getResponseStatus(Exception exception) {
        if (exception instanceof FileNotFoundByIdException) {
            return HttpServletResponse.SC_NOT_FOUND;
        } else if (exception instanceof WrongFileSentException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        } else {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    static class InstantAdapter extends TypeAdapter<Instant> {
        @Override
        public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
            jsonWriter.value(instant.toString());
        }

        @Override
        public Instant read(JsonReader jsonReader) throws IOException {
            return Instant.parse(jsonReader.nextString());
        }
    }
}

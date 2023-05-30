package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.model.FileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class JettyFileServerAppTest {

    private static JettyServer server;
    private static HttpClient client;
    private static Gson gson;

    @BeforeAll
    public static void setup() throws Exception {
        server = new JettyServer();
        server.start();

        client = HttpClient.newHttpClient();

        gson = new GsonBuilder().create();
    }

    @Test
    @DisplayName("Valid upload")
    public void testValidUpload() throws Exception {
        HttpRequest request = uploadFile("test ".repeat(100), "test_valid.txt");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        FileInfo fileInfo = gson.fromJson(response.body(), FileInfo.class);
        assertEquals("test_valid.txt", fileInfo.getName());
    }

    @Test
    @DisplayName("Invalid upload: file too large")
    public void testUploadingTooLargeFile() throws Exception {
        HttpRequest request = uploadFile("test ".repeat(120000), "test_too_large.txt");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("Invalid upload: wrong file extension")
    public void testUploadingWrongTypeOfFile() throws Exception {
        HttpRequest request = uploadFile("test ".repeat(100), "test_wrong_extension.log");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("Download existing file")
    public void testDownloadingExistingFile() throws Exception {
        HttpRequest request = uploadFile("test", "test_valid.txt");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        FileInfo fileInfo = gson.fromJson(response.body(), FileInfo.class);

        request = HttpRequest
                .newBuilder(server.baseUri.resolve("/download/" + fileInfo.getId()))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("test", response.body());
    }

    @Test
    @DisplayName("Download non-existing file")
    public void testDownloadingNonExistingFile() throws Exception {
        HttpRequest request = HttpRequest
                .newBuilder(server.baseUri.resolve("/download/wrongId"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Download list of files")
    public void testDownloadingFileList() throws Exception {
        HttpRequest request = uploadFile("test", "test_valid.txt");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        FileInfo fileInfo = gson.fromJson(response.body(), FileInfo.class);

        request = HttpRequest
                .newBuilder(server.baseUri.resolve("/files"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<FileInfo> fileList = gson.fromJson(response.body(), new TypeToken<List<FileInfo>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertFalse(fileList.isEmpty());
        assertTrue(fileList.stream()
                .anyMatch(f -> fileInfo.getId().equals(f.getId())));
    }

    private HttpRequest uploadFile(String payLoad, String filename) {
        String boundary = "----Boundary" + System.currentTimeMillis();
        String body = multipartRequestBody(boundary, payLoad, filename);

        return HttpRequest
                .newBuilder(server.baseUri.resolve("/upload"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private String multipartRequestBody(String boundary, String payLoad, String filename) {
        return "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" + payLoad + "\r\n" +
                "--" + boundary + "--\r\n";
    }

}

## Jetty file server

### Overview
**Jetty file server** is a simple file service that allows uploading and downloading files.
It has the following endpoints:
- GET /files: get a list of files on the server as an array of JSON objects, containing filename, id and size of the file in KB.
- GET /download/<FILE_ID>: downloads a file by its ID.
- POST /upload: uploads a file as a multipart/form-data
- GET /: simple root page for file uploading

Default implementation of the FileService uploads files to ```uploads``` directory within the project directory. Each file gets a UUID and is kept in a separate directory with the name = UUID.

### Build and run
1. Use ```mvnw clean compile assembly:single``` from the project root to create an executable Jar file.
2. Use ```java -jar ./target/jetty-file-server-1.0-SNAPSHOT-jar-with-dependencies.jar``` to run the application.
3. Use ```Control + C``` to stop the app.
4. You can run tests separately with ```mvnw test``` 

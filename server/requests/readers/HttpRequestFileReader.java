package server.requests.readers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import server.requests.HttpRequestHeaders;
import server.requests.HttpRequestLine;
import server.exceptions.ReadExceptions;


public class HttpRequestFileReader extends HttpReader {
    private final Path path;
    private static final String METHOD = "GET";
    private static final String ROOT = "/data/";

    public HttpRequestFileReader(Path path) {
        this.path = path;
    }

    @Override
    public void read() {
        try {
            // 1. file checking
            File file = path.toFile();
            ReadExceptions readCheck = new ReadExceptions();
            readCheck.checkFile(file);
            String mimeType = readCheck.checkExtension(file);

            // 2. initialize request format
            HttpRequestLine requestLine = new HttpRequestLine(METHOD, ROOT + file.getName());
            HttpRequestHeaders headers = new HttpRequestHeaders();
            headers.addHeader("Host", "localhost:9999");
            headers.addHeader("Content-Type", mimeType);
            headers.addHeader("Content-Length", String.valueOf(file.length()));
            headers.addHeader("Connection", "keep-alive");
            headers.addHeader("User-Agent", "Dummy-Client/0.1");

            String body = (mimeType.startsWith("text/"))? 
                Files.readString(file.toPath()) : "";

            // 3. set request format
            super.getRequest().setRequestLine(requestLine);
            super.getRequest().setHeaders(headers);
            super.getRequest().setBody(body);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // hopefully this displays the http request format we need to send to the server
    public static void main(String[] args) throws IOException {
        HttpRequestFileReader reader = new HttpRequestFileReader(Path.of("data/test.txt"));
        reader.read();
        System.out.println(reader.getRequest());
    }
}
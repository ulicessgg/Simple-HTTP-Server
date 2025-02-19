package server.requests.readers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import server.config.MimeTypes;
import server.requests.HttpRequestFormat;
import server.requests.HttpRequestHeaders;
import server.requests.HttpRequestLine;

public class HttpRequestFileReader extends HttpReader {
    private static final String DATA_DIR = "data";
    private Path path;

    public HttpRequestFileReader(Path path) {
        // must get files from "data/"
        this.path = Paths.get(DATA_DIR).resolve(path.getFileName());
    }

    @Override
    public void read() {
        try {
            // TODO: attempt to read the file from my machine (must come from /data/[something]).
            isDataDirExists();
            if (!isFileExists()) {
                throw new IOException("File not found: " + path.toAbsolutePath());
            }

            // part 1: initialize first line (protocol, path, version)
            HttpRequestLine requestLine = new HttpRequestLine("GET", path.toString(), "HTTP/1.1");

            // part 2: initialize headers
            HttpRequestHeaders headers = new HttpRequestHeaders();
            String mimeType = MimeTypes.getDefault().getMimeTypeFromExtension(getFileExtension(path.toFile()));

            headers.addHeader("Host", "localhost:9999");
            headers.addHeader("Content-Type", (mimeType != null)? mimeType : "application/octet-stream");
            headers.addHeader("Content-Length", String.valueOf(Files.size(path)));

            // part 3: initialize body (if given)
            String fileContent = Files.readString(path);

            HttpRequestFormat requestFormat = new HttpRequestFormat(requestLine);
            requestFormat.setBody(fileContent);
            requestFormat.setHeaders(headers);

            System.out.println(requestFormat);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void isDataDirExists() throws IOException {
        Path dataDir = Paths.get(DATA_DIR);
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
    }

    private boolean isFileExists() {
        return Files.exists(path) && Files.isRegularFile(path);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return (lastDot == -1)? "" : name.substring(lastDot + 1);
    }

    public static void main(String[] args) throws IOException {
        HttpRequestFileReader fileReader = new HttpRequestFileReader(Path.of("data/test.txt"));
        fileReader.read();
        // System.out.println(fileReader.getFilePath());
    }
}
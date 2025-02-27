package server.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import server.exceptions.ReadExceptions;
import server.handlers.handlersConfig.HttpMethod;
import server.requests.HttpRequestFormat;
import server.requests.HttpRequestLine;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;
import server.responses.ResponseHeader;

public class GetHandle {
    private static String body = null;
    private static byte[] binaryContent = null;
    private static long contentLength = 0;

    private static void setGetBodyHandle(File file, String fileType) throws IOException {
        if (fileType.startsWith("text/")) {
            body = Files.readString(file.toPath());
            contentLength = body.length();
        }
        else {
            binaryContent = Files.readAllBytes(file.toPath());
            contentLength = binaryContent.length;
            body = Base64.getEncoder().encodeToString(binaryContent);
        }
    }

    public static HttpResponseFormat handle(HttpRequestFormat request, File file) throws IOException {
        try {
            ReadExceptions readExceptions = new ReadExceptions();
            readExceptions.checkFile(file);
            String fileType = readExceptions.checkExtension(file);

            setGetBodyHandle(file, fileType);

            HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders()
                    .buildResponseHeaders(HttpMethod.GET, fileType, contentLength, file);

            if (!fileType.equals("text/")) {
                headers.addResponseHeader(ResponseHeader.CONTENT_TRANSFER_ENCODING, "binary");
            }

            headers.addResponseHeader(ResponseHeader.LOCATION, "https://developer.mozilla.org/");
            headers.addResponseHeader(ResponseHeader.TRANSFER_ENCODING, "chunked");

            HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.OK);
            return new HttpResponseFormat(line, headers, body);
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                .buildResponseHeaders(HttpMethod.GET,"text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.NOT_FOUND);
                
            return new HttpResponseFormat(errorLine, errorHeaders, null);
        }
    }
    
    // test GET method
    public static void main(String[] args) {
        try {
            File[] testFiles = {
                new File("data/test.txt"),
                new File("data/test.html"),
                new File("data/is_json.json"),
                new File("data/test-html.html")
            };

            System.out.println("\nTesting files...");
            for (File testFile : testFiles) {
                HttpRequestFormat request = new HttpRequestFormat();
                HttpRequestLine line = new HttpRequestLine("GET", "/data/" + testFile.getName());
                request.setRequestLine(line);
                HttpResponseFormat response = GetHandle.handle(request, testFile);
                System.out.println(response.toString());
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

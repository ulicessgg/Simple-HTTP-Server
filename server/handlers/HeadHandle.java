package server.handlers;

import java.io.File;
import java.io.IOException;

import server.exceptions.ReadExceptions;
import server.handlers.handlersConfig.HttpMethod;
import server.requests.HttpRequestFormat;
import server.requests.HttpRequestLine;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;
import server.responses.ResponseHeader;

public class HeadHandle {
    private static final long SIZE_THRESHOLD = 5 * 1024 * 1024; // 5MB

    public static HttpResponseFormat handle(HttpRequestFormat request, File file) throws IOException {
        try {
            ReadExceptions readExceptions = new ReadExceptions();
            readExceptions.checkFile(file);
            String fileType = readExceptions.checkExtension(file);

            long contentLength = file.length();
            HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders()
                .buildResponseHeaders(HttpMethod.HEAD, fileType, contentLength, file);

            if (contentLength > SIZE_THRESHOLD) {
                headers.addResponseHeader(ResponseHeader.WARNING, "File exceeds 5MB");
                headers.addResponseHeader(ResponseHeader.ACCEPT_RANGE, "bytes");
            }
            headers.addResponseHeader(ResponseHeader.TRANSFER_ENCODING, "chunked");

            HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.OK);

            return new HttpResponseFormat(line, headers, null);            
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                .buildResponseHeaders(HttpMethod.HEAD,"text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.NOT_FOUND);
                
            return new HttpResponseFormat(errorLine, errorHeaders, null);
        }
    }
    
    public static void main(String[] args) {
        try {
            File[] testFiles = {
                new File("data/IMG_2811.jpg"),
                new File("data/large-text-file.txt")
            };

            System.out.println("\nTesting files...");
            for (File testFile : testFiles) {
                HttpRequestFormat request = new HttpRequestFormat();
                HttpRequestLine line = new HttpRequestLine("HEAD", "/data/" + testFile.getName());
                request.setRequestLine(line);
                HttpResponseFormat response = HeadHandle.handle(request, testFile);
                System.out.println(response.toString());
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

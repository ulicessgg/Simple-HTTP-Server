package server.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import server.config.MimeTypes;
import server.exceptions.ReadExceptions;
import server.handlers.handlersConfig.HttpMethod;
import server.requests.HttpRequestFormat;
import server.requests.HttpRequestLine;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;
import server.responses.ResponseHeader;

public class DeleteHandle {
    public static HttpResponseFormat handle(HttpRequestFormat request, File file) throws IOException {
        try {
            // part 1: check if the data directory exists
            File dataDir = new File("data");

            // part 2: check if the file requested to be deleted exists. return 404 if not found
            File targetFile = new File(dataDir, file.getName());
            if (!targetFile.exists()) {
                HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders()
                    .buildResponseHeaders(HttpMethod.DELETE, "text/plain", 0, null);
                HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.NOT_FOUND);
                return new HttpResponseFormat(line, headers, null);
            }

            long contentLength = targetFile.length();
            String fileType = MimeTypes.getDefault().getMimeTypeFromExtension(targetFile.getName());
            String body = Files.readString(file.toPath());

            // part 3: check if the file can be deleted. one example of where it can't is authentication. return 500 if we can't
            ReadExceptions checkTargetFile = new ReadExceptions();
            checkTargetFile.checkFile(targetFile);
            checkTargetFile.checkAuthenticate(targetFile);

            // part 4: delete the file
            targetFile.delete();

            // part 5: return response code of 204
            HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders();
            headers.addResponseHeader(ResponseHeader.CONTENT_TYPE, fileType);
            headers.addResponseHeader(ResponseHeader.CONTENT_LENGTH, String.valueOf(contentLength));
            headers.addResponseHeader(ResponseHeader.TRANSFER_ENCODING, "chunked");

            HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.NO_CONTENT);
            return new HttpResponseFormat(line, headers, body);
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                .buildResponseHeaders(HttpMethod.PUT,"text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.INTERNAL_SERVER_ERROR);
                
            return new HttpResponseFormat(errorLine, errorHeaders, "File cannot be deleted");
        }
    }
    public static void main(String[] args) {
        try {
            File testFile = new File("data/test-html.html");
            HttpRequestFormat request = new HttpRequestFormat();
            request.setRequestLine(new HttpRequestLine("DELETE", "/data/" + testFile.getName()));
            HttpResponseFormat response = DeleteHandle.handle(request, testFile);
            System.out.println(response.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

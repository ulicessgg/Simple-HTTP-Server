package server.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import server.handlers.handlersConfig.HandleUtils;
import server.handlers.handlersConfig.HttpMethod;
import server.requests.HttpRequestFormat;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;
import server.responses.ResponseHeader;

public class PutHandle {
    public static HttpResponseFormat handle(HttpRequestFormat request, File file) throws IOException {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }

            File targetFile = new File(dataDir, file.getName());
            String body = request.getRequestBody();

            byte[] contentBytes = (body != null)? body.getBytes() : new byte[0];

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(contentBytes);
            }

            HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders();
            headers.addResponseHeader(ResponseHeader.CONNECTION, "keep-alive");
            headers.addResponseHeader(ResponseHeader.TRANSFER_ENCODING, "chunked");
            headers.addResponseHeader(ResponseHeader.EXPIRES, HandleUtils.getExpirationDate());

            HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.CREATED);
            return new HttpResponseFormat(line, headers, body);
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                .buildResponseHeaders(HttpMethod.PUT,"text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.INTERNAL_SERVER_ERROR);
                
            return new HttpResponseFormat(errorLine, errorHeaders, "File cannot be created");
        }
    }

    public static void main(String[] args) {
        try {
            File helloFile = new File("test-html.html");
            HttpRequestFormat request = new HttpRequestFormat();

            String testBody = "<p>Hello world!</p>\n\n<h1>Large text!!!</h1>";
            request.setRequestBody(testBody);

            HttpResponseFormat response = PutHandle.handle(request, helloFile);
            System.out.println(response.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

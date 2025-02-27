package server.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import server.auth.Authenticator;
import server.config.MimeTypes;
import server.exceptions.ReadExceptions;
import server.handlers.handlersConfig.HandleUtils;
import server.handlers.handlersConfig.HttpMethod;
import server.requests.HttpRequestFormat;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;
import server.responses.ResponseHeader;

public class RequestHandler 
{
    private String documentRoot;
    private MimeTypes mimeTypes;
    private Authenticator authenticator;    // only used for accessing secret files

    // added constructor as handler essentially had no info to work off of besides the socket port
    public RequestHandler(String documentRoot, MimeTypes mimeTypes)
    {
        this.documentRoot = documentRoot;
        this.mimeTypes = mimeTypes;
        String passwordLoc = Paths.get(documentRoot, "secret", ".password").toString();
        this.authenticator = new Authenticator(passwordLoc);
    }
    
    // works now if hard coding responses, left out to implement dynamic
    public void handleRequest(Socket clientSocket)
    {
        try(clientSocket; 
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream())
        {
            //if it works then the returned value should be valid any bad requests left as null
            HttpRequestFormat request = HttpRequestFormat.parse(in);
            if(request != null)
            {
                String filePath = request.getRequestLine().getPath();
                File requestedFile = new File(documentRoot, filePath);

                switch(request.getRequestLine().getMethod())
                {
                    case "GET":
                        handleGet(request, requestedFile);
                        break;
                    case "HEAD":
                        handleHead(request, requestedFile);
                        break;
                    case "PUT":
                        handlePut(request, requestedFile);
                        break;
                    case "DELETE":
                        handleDelete(request, requestedFile);
                        break;
                    default:
                        HttpResponseLine responseLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.BAD_REQUEST);
                        HttpResponseHeaders responseHeaders = new HttpResponseHeaders();
                        HttpResponseFormat response = new HttpResponseFormat(responseLine, responseHeaders, null);
                        out.write(response.toString().getBytes());
                        out.flush();
                        break;
                }
            }
            else    // invalid will result in a 400
            {
                HttpResponseLine responseLine = new HttpResponseLine(null, ResponseCode.BAD_REQUEST);
                HttpResponseHeaders responseHeaders = new HttpResponseHeaders();
                HttpResponseFormat response = new HttpResponseFormat(responseLine, responseHeaders, null);
                out.write(response.toString().getBytes());
                out.flush();
            }
        }
        catch (IOException e)
        {
            System.err.println();   // need to figure out what to say in print
        }
    }

    // Dedicated Request Handlers

    private void setGetBodyHandle(File file, String fileType) throws IOException 
    {
        String body = null;
        byte[] binaryContent = null;
        long contentLength = 0;
        
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

    public HttpResponseFormat handleGet(HttpRequestFormat request, File file) throws IOException 
    {
        String body = null;
        long contentLength = 0;

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

    public HttpResponseFormat handleHead(HttpRequestFormat request, File file) throws IOException 
    {
        long SIZE_THRESHOLD = 5 * 1024 * 1024;
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

    public HttpResponseFormat handlePut(HttpRequestFormat request, File file) throws IOException 
    {
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

    public HttpResponseFormat handleDelete(HttpRequestFormat request, File file) throws IOException {
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
}
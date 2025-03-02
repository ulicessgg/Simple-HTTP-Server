package server.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import server.auth.AuthUtils;
import server.auth.Authenticator;
import server.config.MimeTypes;
import server.exceptions.ReadExceptions;
import server.handlers.handlersConfig.HandleUtils;
import server.handlers.handlersConfig.HttpMethod;
import server.requests.HttpRequestFormat;
import server.requests.HttpRequestLine;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;
import server.responses.ResponseHeader;

public class RequestHandler 
{
    private String documentRoot;
    private Authenticator authenticator;
    private AuthUtils utils;

    // added constructor as handler essentially had no info to work off of besides the socket port
    public RequestHandler(String documentRoot)
    {
        this.documentRoot = documentRoot;
        this.authenticator = new Authenticator(documentRoot);
        this.utils = new AuthUtils(documentRoot);
    }
    
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
                        handleGet(request, requestedFile, out);
                        break;
                    case "HEAD":
                        handleHead(request, requestedFile, out);
                        break;
                    case "PUT":
                        handlePut(request, requestedFile, out);
                        break;
                    case "DELETE":
                        handleDelete(request, requestedFile, out);
                        break;
                    default:
                        handleBad(request, out); 
                        break;
                }
            }
            else    // invalid will result in a 400
            {
                handleBad(request, out); 
            }
        }
        catch (IOException e)
        {
            System.err.println("handleRequest failed to execute");
        }
    }

    // Dedicated Request Handlers

    public HttpResponseFormat handleGet(HttpRequestFormat request, File file, OutputStream out) throws IOException 
    {
        long contentLength = 0;

        try {
            // First, check if GET handler requests authentication. Output the response if they have one.
            if (utils.requiresAuth(file.getName())) {
                String authHeader = request.getRequestHeaders().getRequestHeader("Authorization");
                
                if (authHeader == null) {
                    HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders()
                        .addResponseHeader(ResponseHeader.WWW_AUTHENTICATE, authenticator.getWWWAuthHeader());
                    HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.UNAUTHORIZED);
                    HttpResponseFormat response = new HttpResponseFormat(line, headers, "Authentication required");
                    out.write(response.toString().getBytes());
                    out.flush();
                    return response;
                }

                HttpResponseFormat authResponse = authenticator.handleAuth(authHeader, file);
                if(authResponse != null)
                {
                    out.write(authResponse.toString().getBytes());
                    out.flush();
                    return authResponse;
                }
            }

            // Standard GET request and response for all unrestrictive access to other files.
            ReadExceptions readExceptions = new ReadExceptions();
            readExceptions.checkFile(file);

            String fileType = readExceptions.checkExtension(file);

            byte[] content = Files.readAllBytes(file.toPath());
            contentLength = content.length;
            HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders()
                 .buildResponseHeaders(HttpMethod.GET, fileType, contentLength, file);

            HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.OK);
            HttpResponseFormat response = new HttpResponseFormat(line, headers, null);

            out.write(response.toString().getBytes());
            out.write(content);
            out.flush();
            return response;
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                    .buildResponseHeaders(HttpMethod.GET, "text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.NOT_FOUND);
            HttpResponseFormat response = new HttpResponseFormat(errorLine, errorHeaders, null);
            out.write(response.toString().getBytes());
            out.flush();
            return response;
        }
    }

    public HttpResponseFormat handleHead(HttpRequestFormat request, File file, OutputStream out) throws IOException 
    {
        long SIZE_THRESHOLD = 5 * 1024 * 1024; // Restrict size of HEAD request to be 5MB.
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
            HttpResponseFormat response = new HttpResponseFormat(line, headers, null);
            out.write(response.toString().getBytes());
            out.flush();
            return response;           
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                    .buildResponseHeaders(HttpMethod.HEAD, "text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.NOT_FOUND);
            HttpResponseFormat response = new HttpResponseFormat(errorLine, errorHeaders, null);
            out.write(response.toString().getBytes());
            out.flush();
            return response;
        }
    }

    public HttpResponseFormat handlePut(HttpRequestFormat request, File file, OutputStream out) throws IOException 
    {
        try {
            String filePath = file.getPath();
            String targetDir = "";
            
            Path pathToRoot = Paths.get(documentRoot);
            Path pathToFile = Paths.get(filePath);

            if (pathToFile.startsWith(pathToRoot)) {
                Path relativePath = pathToRoot.relativize(pathToFile);
                if (relativePath.getNameCount() > 1) {
                    targetDir = relativePath.getParent().toString();
                }
            }

            File putDir = new File(documentRoot, targetDir);

            if (!putDir.exists()) {
                putDir.mkdirs();
            }

            File targetFile = new File(putDir, file.getName());

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
            HttpResponseFormat response = new HttpResponseFormat(line, headers, body);
            out.write(response.toString().getBytes());
            out.flush();
            return response;
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                    .buildResponseHeaders(HttpMethod.PUT, "text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.INTERNAL_SERVER_ERROR);
            HttpResponseFormat response = new HttpResponseFormat(errorLine, errorHeaders, "File cannot be created");
            out.write(response.toString().getBytes());
            out.flush();
            return response;
        }
    }

    public HttpResponseFormat handleDelete(HttpRequestFormat request, File file, OutputStream out) throws IOException {
        try {
            // part 1: find path to file to be deleted 
            String filePath = file.getPath();
            String targetDir = "";

            if (filePath.contains("/")) {
                targetDir = filePath.substring(0, filePath.lastIndexOf("/"));
            }

            Path documentRootPath = Paths.get(documentRoot);
            Path filePathPath = Paths.get(filePath);

            if (filePathPath.startsWith(documentRootPath)) {
                Path relativePath = documentRootPath.relativize(filePathPath);
                if (relativePath.getNameCount() > 1) {
                    targetDir = relativePath.getParent().toString();
                }
            }
        
            File dataDir = new File(documentRoot, targetDir);

            // part 2: check if the file requested to be deleted exists. return 404 if not found
            File targetFile = new File(dataDir, file.getName());
            if (!targetFile.exists()) {
                HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders()
                        .buildResponseHeaders(HttpMethod.DELETE, "text/plain", 0, null);
                HttpResponseLine line = new HttpResponseLine(request.getRequestLine(), ResponseCode.NOT_FOUND);
                HttpResponseFormat response = new HttpResponseFormat(line, headers, null);
                out.write(response.toString().getBytes());
                out.flush();
                return response;
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
            HttpResponseFormat response = new HttpResponseFormat(line, headers, body);
            out.write(response.toString().getBytes());
            out.flush();
            return response;
        }
        catch (Exception e) {
            HttpResponseHeaders errorHeaders = HttpResponseHeaders.createResponseHeaders()
                    .buildResponseHeaders(HttpMethod.PUT, "text/plain", 0, null);

            HttpResponseLine errorLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.INTERNAL_SERVER_ERROR);
            HttpResponseFormat response = new HttpResponseFormat(errorLine, errorHeaders, "File cannot be deleted");
            out.write(response.toString().getBytes());
            out.flush();
            return response;
        }
    }

    public void handleBad(HttpRequestFormat request, OutputStream out) throws IOException {
        HttpRequestLine requestLine = new HttpRequestLine("ERROR", "/400");
        HttpResponseLine responseLine = new HttpResponseLine(requestLine, ResponseCode.BAD_REQUEST);
        HttpResponseHeaders responseHeaders = HttpResponseHeaders.createResponseHeaders()
                .addResponseHeader(ResponseHeader.CONTENT_TYPE, "text/plain")
                .addResponseHeader(ResponseHeader.CONTENT_LENGTH, "")
                .addResponseHeader(ResponseHeader.CONNECTION, "close");

        HttpResponseFormat response = new HttpResponseFormat(responseLine, responseHeaders, null);
        out.write(response.toString().getBytes());
        out.flush();
    }
}
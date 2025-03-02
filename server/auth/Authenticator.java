package server.auth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.handlers.handlersConfig.HttpMethod;
import server.requests.HttpRequestLine;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;
import server.responses.ResponseHeader;

public class Authenticator {
    private Map<String, String> credentials;
    private static final String REALM = "667 Server";
    private String documentRoot;

    public Authenticator(String documentRoot) {
        this.credentials = new HashMap<>();
        this.documentRoot = documentRoot;
    }

    public boolean isAuth(String authHeader, File passwordFile) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }

        // Decode base64 string, and check password file if matching. 
        try {
            String base64Cred = authHeader.substring("Basic ".length()).trim();
            String cred = new String(Base64.getDecoder().decode(base64Cred));
            String[] parts = cred.split(":");

            if (parts.length != 2) return false;

            String username = parts[0];
            String password = parts[1];

            List<String> lines = Files.readAllLines(passwordFile.toPath());

            for(String line: lines)
            {
                String[] credentials = line.split(":");
                if(credentials.length == 2 && credentials[0].trim().equals(username) && credentials[1].trim().equals(password))
                {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public String getWWWAuthHeader() {
        return String.format("Basic realm=\"%s\"", REALM);
    }

    public HttpResponseFormat handleAuth(String authHeader, File passwordFile) {
        HttpRequestLine requestLine = new HttpRequestLine("GET", passwordFile.getPath());

        HttpResponseHeaders headers = HttpResponseHeaders.createResponseHeaders()
            .buildResponseHeaders(HttpMethod.GET, "text/plain", 0, null);
        
        // Cannot access restricted resource. Have user login.
        if (authHeader == null) {
            headers.addResponseHeader(ResponseHeader.WWW_AUTHENTICATE, getWWWAuthHeader());
            HttpResponseLine line = new HttpResponseLine(requestLine, ResponseCode.UNAUTHORIZED);
            String authBody = "Authentication required";
            return new HttpResponseFormat(line, headers, authBody);
        }

        // You cannot access this resource, even with correct username:password
        if (!isAuth(authHeader, passwordFile)) {
            HttpResponseLine line = new HttpResponseLine(requestLine, ResponseCode.FORBIDDEN);
            String authBody = "Access denied";
            return new HttpResponseFormat(line, headers, authBody);
        }

        try {
            String content = Files.readString(passwordFile.toPath());
            headers.addResponseHeader(ResponseHeader.CONTENT_LENGTH, String.valueOf(content.length()));
            HttpResponseLine line = new HttpResponseLine(requestLine, ResponseCode.OK);
            return new HttpResponseFormat(line, headers, content);
        }
        catch (IOException e) {
            HttpResponseLine line = new HttpResponseLine(requestLine, ResponseCode.INTERNAL_SERVER_ERROR);
            String errorBody = "Can't read password file";
            return new HttpResponseFormat(line, headers, errorBody);
        }
    }
}

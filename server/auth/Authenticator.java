package server.auth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
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

    public Authenticator() {
        this.credentials = new HashMap<>();
        AuthUtils.loadPasswordFile(credentials);
    }

    private boolean isAuth(String authHeader) {
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

            return (this.credentials.containsKey(username) &&
                    this.credentials.get(username).equals(password));
        }
        catch (Exception e) {
            return false;
        }
    }

    private String getWWWAuthHeader() {
        return String.format("Basic realm=\"%s\"", REALM);
    }

    public HttpResponseFormat handleAuth(String authHeader) {
        HttpRequestLine requestLine = new HttpRequestLine("GET", "/a-secret/files.password");

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
        if (!isAuth(authHeader)) {
            HttpResponseLine line = new HttpResponseLine(requestLine, ResponseCode.FORBIDDEN);
            String authBody = "Access denied";
            return new HttpResponseFormat(line, headers, authBody);
        }

        try {
            String content = Files.readString(new File("a-secret/files.password").toPath());
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

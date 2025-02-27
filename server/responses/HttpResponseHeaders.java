package server.responses;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import server.handlers.handlersConfig.HandleUtils;
import server.handlers.handlersConfig.HttpMethod;

public class HttpResponseHeaders {
    private final Map<String, String> headers;

    public HttpResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    // public void addResponseHeader(String key, String value) {
    //     if (key != null && value != null) headers.put(key, value);
    // }

    public HttpResponseHeaders addResponseHeader(ResponseHeader header, String value) {
        if (value != null) {
            headers.put(header.getHeaderName(), value);
        }

        return this;
    }

    public String getResponseHeader(String key) {
        return headers.get(key);
    }

    public Set<Map.Entry<String, String>> getResponseHeaders() {
        return headers.entrySet();
    }

    public static HttpResponseHeaders createResponseHeaders() {
        HttpResponseHeaders headers = new HttpResponseHeaders();
        headers.addResponseHeader(ResponseHeader.DATE, HandleUtils.getCurrentDate())
                .addResponseHeader(ResponseHeader.SERVER, "Server667");

        return headers;
    }

    public HttpResponseHeaders buildResponseHeaders(HttpMethod method, String contentType, long contentLength, File file) {
        this.addResponseHeader(ResponseHeader.CONTENT_TYPE, contentType)
            .addResponseHeader(ResponseHeader.CONTENT_LENGTH, String.valueOf(contentLength))
            .addResponseHeader(ResponseHeader.CONNECTION, "keep-alive");

        if (file != null && file.exists()) {
            this.addResponseHeader(ResponseHeader.LAST_MODIFIED, HandleUtils.getModifiedDate(file));
            this.addResponseHeader(ResponseHeader.EXPIRES, HandleUtils.getExpirationDate());
        }

        return this;
    }

    // public static HttpResponseHeaders generateResponseHeaders(String method, String contentType, long contentLength, String location) {
    //     HttpResponseHeaders headers = new HttpResponseHeaders();

    //     switch(method) {
    //         case "GET", "HEAD" -> {
    //             headers.addResponseHeader(ResponseHeader.CONTENT_TYPE.getHeaderName(), contentType);
    //             headers.addResponseHeader(ResponseHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(contentLength));
    //             headers.addResponseHeader(ResponseHeader.CONNECTION.getHeaderName(), "keep-alive");
    //         }
    //         case "POST", "PUT" -> {
    //             headers.addResponseHeader(ResponseHeader.CONTENT_TYPE.getHeaderName(), contentType);
    //             headers.addResponseHeader(ResponseHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(contentLength));
    //             if (location != null) {
    //                 headers.addResponseHeader(ResponseHeader.LOCATION.getHeaderName(), location);
    //             }
    //         }
    //         case "DELETE" -> {
    //             headers.addResponseHeader(ResponseHeader.CONTENT_TYPE.getHeaderName(), contentType);
    //             headers.addResponseHeader(ResponseHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(contentLength));
    //         }
    //     }

    //     return headers;
    // }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append("\r\n");
        }

        return sb.toString().trim();
    }

    // public static void main(String[] args) {
    //     HttpResponseHeaders headers = HttpResponseHeaders.generateResponseHeaders("DELETE", "text/html", 1234, null);
    //     System.out.println(headers.toString());
    // }
}

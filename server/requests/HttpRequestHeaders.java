package server.requests;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpRequestHeaders {
    // whatever you need for the headers.
    private final Map<String, String> headers;

    public HttpRequestHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public static HttpRequestHeaders createHeader() 
    {
        return new HttpRequestHeaders();
    }

    public static HttpRequestHeaders createHeader(String host, String contentType, long contentLength) {
        HttpRequestHeaders headers = new HttpRequestHeaders();
        headers.addRequestHeader("Host", host);
        headers.addRequestHeader("Content-Type", contentType);
        headers.addRequestHeader("Content-Length", String.valueOf(contentLength));
        headers.addRequestHeader("Connection", "keep-alive");
        headers.addRequestHeader("User-Agent", "Dummy-Client/0.1");
        return headers;
    }

    public static HttpRequestHeaders createHeader(String host, String contentType, long contentLength, String connection, String user) {
        HttpRequestHeaders headers = new HttpRequestHeaders();
        headers.addRequestHeader("Host", host);
        headers.addRequestHeader("Content-Type", contentType);
        headers.addRequestHeader("Content-Length", String.valueOf(contentLength));
        headers.addRequestHeader("Connection", connection);
        headers.addRequestHeader("User-Agent", user);
        return headers;
    }

    public Set<Entry<String, String>> getRequestHeaders() {
        return headers.entrySet();
    }

    public void addRequestHeader(String key, String value) {
        if (key != null && value != null) headers.put(key, value);
    }

    public String getRequestHeader(String key) {
        return headers.get(key);
    } 

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append("\r\n");
        }
        return sb.toString();
    }

    // test RequestHeaders class
    public static void main(String[] args) {
        HttpRequestHeaders emptyHeader = HttpRequestHeaders.createHeader();
        System.out.println(emptyHeader);
        
        HttpRequestHeaders header = HttpRequestHeaders.createHeader("localhost:9999", "text/html", 100);
        System.out.println(header);
    }
}
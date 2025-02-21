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

    public HttpRequestHeaders(String host, String contentType, long contentLength) {
        this(); // will instantiate the empty constructor for the headers map
        addHeader("Host", host);
        addHeader("Content-Type", contentType);
        addHeader("Content-Length", String.valueOf(contentLength));
    }

    public static HttpRequestHeaders defaultHeaders(String host, String contentType, long contentLength) {
        HttpRequestHeaders headers = new HttpRequestHeaders();
        headers.addHeader("Host", host);
        headers.addHeader("Content-Type", contentType);
        headers.addHeader("Content-Length", String.valueOf(contentLength));
        headers.addHeader("Connection", "keep-alive");
        headers.addHeader("User-Agent", "Dummy-Client/0.1");
        return headers;
    }

    public Set<Entry<String, String>> getHeaders() {
        return headers.entrySet();
    }

    public void addHeader(String key, String value) {
        if (key != null && value != null) headers.put(key, value);
    }

    public String getHeader(String key) {
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
        HttpRequestHeaders headers = HttpRequestHeaders.defaultHeaders("localhost:9999", "text/html", 100);
        System.out.println(headers);
    }
}

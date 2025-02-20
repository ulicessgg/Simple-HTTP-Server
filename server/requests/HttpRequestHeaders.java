package server.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

public class HttpRequestHeaders {
    // whatever you need for the headers.
    // Host: localhost:8080/add-images
    private Map<String, String> headers;

    private String host; // where do you host your content
    private String contentType; // MIME-type
    private long contentLength; // how many bytes is this content
    private String userAgent; // who's sending the request
    private String auth; // where do you access resources from
    private String accept; // expected MIME type of that content (use the MIME class)
    private String connection; // how long should this resource be connected to host

    public HttpRequestHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpRequestHeaders(String host, String contentType, long contentLength) {
        this(); // will instantiate the empty constructor for the headers map
        this.host = host;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public Set<Entry<String, String>> getHeaders() {
        return headers.entrySet();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    } 

    // is there a way to simplify this? - Maybe try a map? then you can use gets for appending - Ulices
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (host != null) sb.append("Host: ").append(host).append("\r\n");
        if (contentType != null) sb.append("Content-Type: ").append(contentType).append("\r\n");
        if (contentLength > 0) sb.append("Content-Length: ").append(contentLength).append("\r\n");
        if (userAgent != null) sb.append("User-Agent: ").append(userAgent).append("\r\n");
        if (auth != null) sb.append("Authorization: ").append(auth).append("\r\n");
        if (accept != null) sb.append("Accept: ").append(accept).append("\r\n");
        if (connection != null) sb.append("Connection: ").append(connection).append("\r\n");

        for (Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        return sb.toString().trim();
    }
}

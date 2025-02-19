package server.requests;

public class HttpRequestLine {
    // GET /some/thing/to/send.pdf HTTP/1.1\r\n
    private String method; 
    // your path here
    private String path; 
    // strictly, HTTP/1.1
    private String version;

    public HttpRequestLine() {}

    public HttpRequestLine(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return method + " " + path + " " + version;
    }
}
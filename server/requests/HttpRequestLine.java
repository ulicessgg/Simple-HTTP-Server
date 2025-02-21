package server.requests;

public class HttpRequestLine {
    // GET /some/thing/to/send.pdf HTTP/1.1\r\n
    private String method; 
    // your path here
    private String path; 
    // strictly, HTTP/1.1
    private String version;

    public HttpRequestLine(String method, String path) {
        this.method = method;
        this.path = path;
        this.version = "HTTP/1.1";
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return method + " " + path + " " + version;
    }

    // test RequestLine class
    public static void main(String[] args) {
        HttpRequestLine line = new HttpRequestLine("GET", "/data/test.txt");
        System.out.println(line);
    }
}
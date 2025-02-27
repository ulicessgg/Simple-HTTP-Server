package server.requests;

public class HttpRequestLine {
    private String method; 
    private String path; 
    private static final String VERSION = "HTTP/1.1";

    public HttpRequestLine(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return VERSION;
    }

    @Override
    public String toString() {
        return method + " " + path + " " + VERSION;
    }

    // test RequestLine class
    public static void main(String[] args) {
        HttpRequestLine line = new HttpRequestLine("GET", "/data/test.txt");
        System.out.println(line.toString());
    }
}
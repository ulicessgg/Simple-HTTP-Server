package server.handlers.handlersConfig;

public enum HttpMethod {
    GET("GET"), HEAD("HEAD"), PUT("PUT"), DELETE("DELETE");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}

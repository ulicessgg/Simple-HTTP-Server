package server.responses;

import server.auth.Authenticator;
import server.requests.HttpRequestLine;

public class HttpResponseFormat {
    private final HttpResponseLine responseLine;

    private final HttpResponseHeaders responseHeaders;

    private String body;

    public HttpResponseFormat(HttpResponseLine responseLine, HttpResponseHeaders responseHeaders, String body) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.body = (body == null)? "" : body;
    }

    public HttpResponseLine getResponseLine() {
        return responseLine;
    }

    public HttpResponseHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return body;
    }

    public void setResponseBody(String body) {
        this.body = body;
    }

    public static HttpResponseFormat toAuth(Authenticator auth, String authHeader) {
        return auth.handleAuth(authHeader);
    }

    @Override
    public String toString() {
        return responseLine.toString() + "\r\n" +
                responseHeaders.toString() + "\r\n" +
                "\r\n" +
                body;
    }

    public static void main(String[] args) {
        HttpRequestLine requestLine = new HttpRequestLine("PUT", "/data/test.txt");
        HttpResponseLine responseLine = new HttpResponseLine(requestLine, ResponseCode.OK);
        HttpResponseHeaders responseHeaders = new HttpResponseHeaders();
        String responseBody = "hello\n\nthis is a piece of text";

        HttpResponseFormat response = new HttpResponseFormat(responseLine, responseHeaders, responseBody);
        System.out.println(response.toString());
    }
}

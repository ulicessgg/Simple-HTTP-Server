package server.responses;

import server.requests.HttpRequestLine;

public class HttpResponseLine {
    private final HttpRequestLine requestLineVersion;
    private final ResponseCode responseCode;

    public HttpResponseLine(HttpRequestLine requestLineVersion, ResponseCode responseCode) {
        this.requestLineVersion = requestLineVersion;
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return requestLineVersion.getVersion() + " " + responseCode.getStatus();
    }

    public static void main(String[] args) {
        // note the first argument of this class should not matter, so long as we have our first line of the response.
        HttpResponseLine responseLine = new HttpResponseLine(new HttpRequestLine("HEAD", "/"), ResponseCode.UNAUTHORIZED);
        System.out.println(responseLine.toString());
    }
}

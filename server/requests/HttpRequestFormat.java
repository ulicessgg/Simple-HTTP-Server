package server.requests;

public class HttpRequestFormat {
    private HttpRequestLine requestLine;

    private HttpRequestHeaders headers;

    // what you send to the server if document is specified
    private String body;

    public HttpRequestFormat() {
        this(null);
    }

    public HttpRequestFormat(HttpRequestLine requestLine) {
        this.requestLine = requestLine;
        this.headers = new HttpRequestHeaders();
    }

    // TODO: must parse format 
    // public static HttpRequestFormat parse(BufferedReader reader) throws IOException {
    //     // part 1: initialize first part (method path version\r\n) w/ carriage return

    //     // part 2: initialize second part (Map<String, String> headers \r\n)

    //     // part 3: initialize the third part (\r\n)

    //     // part 4: initialize the fourth part (if given). note no carriage return

    // }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpRequestHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return (body == null)? "" : body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setRequestLine(HttpRequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public void setHeaders(HttpRequestHeaders headers) {
        this.headers = headers;
    }

    // return the http formatted string (idk if this necessary) - Itll prob work for debug and tests - Ulices
    @Override
    public String toString() {
        return String.format(
            requestLine.toString() + "\r\n" +
            headers.toString() + "\r\n" +
            "\r\n" +
            getBody()
        );
    }
}
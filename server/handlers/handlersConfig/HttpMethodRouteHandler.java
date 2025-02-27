package server.handlers.handlersConfig;

import java.io.File;
import java.io.IOException;
import server.handlers.RequestHandler;
import server.requests.HttpRequestFormat;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;

public class HttpMethodRouteHandler {
    private final HttpMethod httpMethod;
    private final RequestHandler handler;

    public HttpMethodRouteHandler(HttpMethod httpMethod, RequestHandler handler) {
        this.httpMethod = httpMethod;
        this.handler = handler;
    }

    public HttpResponseFormat handleRequest(HttpRequestFormat request, File file) throws IOException {
        switch (httpMethod) {
            case GET -> {
                return handler.handleGet(request, file);
            }

            case PUT -> {
                return handler.handlePut(request, file);
            }

            case HEAD -> {
                return handler.handleHead(request, file);
            }

            case DELETE -> {
                return handler.handleDelete(request, file);
            }

            default -> {
                HttpResponseLine responseLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.BAD_REQUEST);
                HttpResponseHeaders responseHeaders = new HttpResponseHeaders();
                return new HttpResponseFormat(responseLine, responseHeaders, null);
            }
        }
    }
}

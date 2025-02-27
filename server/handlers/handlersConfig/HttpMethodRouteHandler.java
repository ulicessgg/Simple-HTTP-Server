package server.handlers.handlersConfig;

import java.io.File;
import java.io.IOException;

import server.handlers.DeleteHandle;
import server.handlers.GetHandle;
import server.handlers.HeadHandle;
import server.handlers.PutHandle;
import server.requests.HttpRequestFormat;
import server.responses.HttpResponseFormat;
import server.responses.HttpResponseHeaders;
import server.responses.HttpResponseLine;
import server.responses.ResponseCode;

public class HttpMethodRouteHandler {
    private final HttpMethod httpMethod;

    public HttpMethodRouteHandler(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HttpResponseFormat handleRequest(HttpRequestFormat request, File file) throws IOException {
        // TODO: each handle is spaghetti code, but these work. fix this after testing.
        switch (httpMethod) {
            case GET -> {
                return GetHandle.handle(request, file);
            }

            case PUT -> {
                return PutHandle.handle(request, file);
            }

            case HEAD -> {
                return HeadHandle.handle(request, file);
            }

            case DELETE -> {
                return DeleteHandle.handle(request, file);
            }

            default -> {
                HttpResponseLine responseLine = new HttpResponseLine(request.getRequestLine(), ResponseCode.BAD_REQUEST);
                HttpResponseHeaders responseHeaders = new HttpResponseHeaders();
                return new HttpResponseFormat(responseLine, responseHeaders, null);
            }
        }
    }
}

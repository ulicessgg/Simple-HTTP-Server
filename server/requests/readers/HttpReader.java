package server.requests.readers;

import server.requests.HttpRequestFormat;

// we have to read the files specified by MIME-type. one abstract class to read the files
// is enough. any file can be read by calling the read() function. if we need info, we
// just get the metadata off that file, and then substitute it in the request format
public abstract class HttpReader {
    private final HttpRequestFormat request;

    public HttpReader() {
        this.request = new HttpRequestFormat();
    }

    // read returns void when a file exists and has been read
    public abstract void read() throws Exception;

    public HttpRequestFormat getRequest() {
        return request;
    }
}

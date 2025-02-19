package server.requests.readers;

import java.nio.file.Path;

import server.requests.HttpRequestFormat;

public abstract class HttpReader {
    HttpRequestFormat requestFormat;

    public HttpReader() {
        this.requestFormat = new HttpRequestFormat();
    }
    // protected Path path;

    // public HttpReader(Path path) {
    //     this.path = path;
    // }

    // read returns void when a file exists and has been read
    public abstract void read() throws Exception;

    public Path getFilePath() {
        return path;
    }
}

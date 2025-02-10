package server;

import java.net.ServerSocket;

import server.config.MimeTypes;

public class WebServer implements AutoCloseable {

    private ServerSocket serverSocket;

    public static void main(String[] args) throws NumberFormatException, Exception {
        if (args.length != 2) {
            System.err.println("usage: java WebServer <port number> <document root>");
            System.exit(1);
        }

        try (WebServer server = new WebServer(
                Integer.parseInt(args[0]),
                args[1], MimeTypes.getDefault())) {
            server.listen();
        }
    }

    public WebServer(int port, String documentRoot, MimeTypes mimeTypes) {

    }

    /**
     * After the webserver instance is constructed, this method will be
     * called to begin listening for requestd
     */
    public void listen() {

        // Feel free to change this logic
        while (true) {
            // Handle a request
        }
    }

    @Override
    public void close() throws Exception {
        this.serverSocket.close();
    }
}
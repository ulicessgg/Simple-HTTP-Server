package server;

import java.net.ServerSocket;
import java.net.Socket; // needed for users to receive from server
import java.IOException;

// I think this should work to create threads
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// will help with responses and requests
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import server.config.MimeTypes; // leave as is 
import server.requests.HttpRequestFormat; // links to kenny's files
import server.requests.HttpRequestHeaders;
import server.requests.HttpRequestLine;

public class WebServer implements AutoCloseable {

    private ServerSocket serverSocket; // leave as is
    private ExecutorService threadPool;
    private String documentRoot;
    private MimeTypes mimeTypes;

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

    public WebServer(int port, String documentRoot, MimeTypes mimeTypes) 
    {
        this.serverSocket = new ServerSocket(port); // allows user port number to be used
        this.threadPool = Executors.newFixedThreadPool(4); // for now gonna leave it as 4
        this.documentRoot = documentRoot;
        this.mimeTypes = mimeTypes;
    }

    /**
     * After the webserver instance is constructed, this method will be
     * called to begin listening for requestd
     */
    public void listen() {
        while (true) 
        {
            // Handle a request
            try 
            {
                Socket clientSocket = serverSocket.accept();
                // need to implement handlers here
            }
            catch (IOException e)
            {
                System.err.println();
            }
        }
    }

    // gonna try and implement using comment logic for parse() in HttpRequestFormat.java WORK IN PROGRESS
    public void handleRequest(Socket clientSocket)
    {

    }

    @Override
    public void close() throws Exception {
        this.serverSocket.close();
    }
}
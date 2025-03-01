package server;

import java.io.IOException; // needed for users to receive from server
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server.config.MimeTypes; // leave as is
import server.handlers.RequestHandler;

public class WebServer implements AutoCloseable {

    private ServerSocket serverSocket; // leave as is
    private ExecutorService threadPool;
    private String documentRoot; // ignore the warning its passed to the handler
    private MimeTypes mimeTypes; // ignore this too 
    private RequestHandler handler;

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

    public WebServer(int port, String documentRoot, MimeTypes mimeTypes) throws IOException
    {
        this.serverSocket = new ServerSocket(port); // allows any port number, have tested
        this.threadPool = Executors.newFixedThreadPool(10); // for now gonna leave it as 10
        this.documentRoot = documentRoot;   
        this.mimeTypes = mimeTypes;  
        this.handler = new RequestHandler(documentRoot); 
    }

    /**
     * After the webserver instance is constructed, this method will be
     * called to begin listening for request
     */
    public void listen() 
    {
        while (true) 
        {
            // Handle a request
            try 
            {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handler.handleRequest(clientSocket));
            }
            catch (IOException e)
            {
                System.err.println("Listen didnt pass request to handler");  
            }
        }
    }

    @Override
    public void close() throws Exception 
    {
        this.serverSocket.close();
        threadPool.shutdown();
    }
}
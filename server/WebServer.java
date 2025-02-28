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
    private String documentRoot;
    private MimeTypes mimeTypes;
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
        this.threadPool = Executors.newFixedThreadPool(10); // for now gonna leave it as 4
        this.documentRoot = documentRoot;   // gives warning but is used for handler
        this.mimeTypes = mimeTypes;   // gives warning but is used for handler
        this.handler = new RequestHandler(documentRoot); // overlooked this was missing
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
                // this works now but with hard coded responses still figuring out for dynamic
                threadPool.submit(() -> handler.handleRequest(clientSocket));
            }
            catch (IOException e)
            {
                System.err.println();   // need to figure out what to say in print
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
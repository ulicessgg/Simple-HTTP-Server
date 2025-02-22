package server;

import java.io.BufferedReader;
import java.io.IOException; // needed for users to receive from server
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server.config.MimeTypes; // leave as is
import server.requests.HttpRequestFormat; // links to kenny's files

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

    public WebServer(int port, String documentRoot, MimeTypes mimeTypes) throws IOException
    {
        this.serverSocket = new ServerSocket(port); // allows user port number to be used
        this.threadPool = Executors.newFixedThreadPool(4); // for now gonna leave it as 4
        this.documentRoot = documentRoot;
        this.mimeTypes = mimeTypes;
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
                threadPool.submit(() -> handleRequest(clientSocket)); // gonna try and use lambda to isolate bugs if any
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
        try(clientSocket; 
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream())
            {
                HttpRequestFormat request = HttpRequestFormat.parse(in); // parse isnt implemented so may need to rewrite or adjust this, if it works then the returned value should be valid
                                                                         // and any bad requests will essentially be left as null
                // valid requests will then be handled accordingly 
                if(request != null)
                {
                    // wait till parse is implemented in order to make sure this can be attempted
                }
                else    // invalid will result in a 400
                {
                    String error = String.format("HTTP/1.1 %d %s\r\n\r\n", 400, "Bad Request");
                    out.write(error.getBytes());
                    out.flush();
                }
            }
        catch (IOException e)
            {
                System.err.println();
            }
    }

    @Override
    public void close() throws Exception 
    {
        this.serverSocket.close();
        threadPool.shutdown();
    }
}
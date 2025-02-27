package server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files; // leave these we need em to access requested files
import java.nio.file.Path;
import java.nio.file.Paths;

import server.config.MimeTypes;
import server.requests.HttpRequestFormat;
import server.auth.Authenticator;

public class RequestHandler 
{
    private String documentRoot;
    private MimeTypes mimeTypes;
    private Authenticator authenticator;    // only used for accessing secret files

    // added constructor as handler essentially had no info to work off of besides the socket port
    public RequestHandler(String documentRoot, MimeTypes mimeTypes)
    {
        this.documentRoot = documentRoot;
        this.mimeTypes = mimeTypes;
        String passwordLoc = Paths.get(documentRoot, "secret", ".password").toString();
        this.authenticator = new Authenticator(passwordLoc);
    }
    
    // works now if hard coding responses, left out to implement dynamic
    public void handleRequest(Socket clientSocket)
    {
        try(clientSocket; 
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream())
        {
            //if it works then the returned value should be valid any bad requests left as null
            HttpRequestFormat request = HttpRequestFormat.parse(in);
            if(request != null)
            {
                // gather info from request to find the path to a file
                String file = request.getRequestLine().getPath();
                String pathToFile = documentRoot + file;
                Path path = Paths.get(pathToFile);

                if (Files.exists(path) && !Files.isDirectory(path)) // check if it even exists
                {
                    byte[] data = Files.readAllBytes(path);

                    // implemented helper below and using mimehelper gets us the file type
                    String extension = getExtension(file);
                    String contentType = mimeTypes.getMimeTypeFromExtension(extension);

                    //response called/created here

                    //out.write(response.getBytes());
                    out.write(data);
                    out.flush();
                } 
                else    
                {
                    //response called/created here
                    //out.write(error.getBytes());
                    out.flush();
                }
            }
            else    // invalid will result in a 400
            {
                //response called/created here
                //out.write(error.getBytes());
                out.flush();
            }
        }
        catch (IOException e)
        {
            System.err.println();   // need to figure out what to say in print
        }
    }

    public String getExtension(String file) 
    {
        // finds the . in a given file name, used lastIndex just incase the name is or has a .
        int dot = file.lastIndexOf(".");
        if (dot > 0 && dot < file.length() - 1) // this just makes sure the . is in the right spot
        {
            return file.substring(dot + 1); // this would be the extension
        }
        else
        {
            return null;
        }
    }
}
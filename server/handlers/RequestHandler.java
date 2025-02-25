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
import server.requests.HttpRequestHeaders;
import server.requests.HttpRequestLine;

public class RequestHandler 
{
    private String documentRoot;
    private MimeTypes mimeTypes;

    // added constructor as handler essentially had no info to work off of besides the socket port
    public RequestHandler(String documentRoot, MimeTypes mimeTypes)
    {
        this.documentRoot = documentRoot;
        this.mimeTypes = mimeTypes;
    }
    
    // this is for when we want the server to read the info
    public HttpRequestFormat parse(BufferedReader reader) throws IOException 
    {
        try 
        {
            // part 1: initialize first part (method path version\r\n) w/ carriage return
            String line = reader.readLine();
            if(line == null)
            {
                return null;    // client error
            }

            String[] lineSegments = line.split(" ");
            if(lineSegments.length != 3)
            {
                return null;    // line is invalid
            }

            HttpRequestLine requestLine = new HttpRequestLine(lineSegments[0], lineSegments[1]);

            // part 2: initialize second part (Map<String, String> headers \r\n)
            HttpRequestHeaders header = new HttpRequestHeaders();
            while((line = reader.readLine()) != null && !line.isEmpty())
            {
                // part 3: initialize the third part (\r\n)
                int index = line.indexOf(": ");
                if(index > 0)
                {
                    header.addHeader(line.substring(0, index), line.substring(index + 2));
                }
            }

            // part 4: initialize the fourth part (if given). note no carriage return
            String conLenStr = header.getHeader("Content-Length");
            StringBuilder body = new StringBuilder();
            if(conLenStr != null)
            {
                try
                {
                    int contentLength = Integer.parseInt(conLenStr);
                    for(int i = 0; i < contentLength; i++)
                    {
                        int temp = reader.read();
                        if(temp == -1)
                        {
                            break;  // eof
                        }
                        body.append((char) temp);
                    }
                }
                catch(NumberFormatException e)
                {
                    return null; // content length invalid
                }
            }

            return new HttpRequestFormat(requestLine, header, body.toString());
        }

        catch (IOException e) 
        {
        return null;
        }
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
                    // leaving this out for now since not all requests require data output
                    // byte[] data = Files.readAllBytes(path);

                    // implemented helper below and using mimehelper gets us the file type
                    String extension = getExtension(file);
                    String fileType = mimeTypes.getMimeTypeFromExtension(extension);

                    String response = ""; // leaving blank need to figure out dynamic responses

                    out.write(response.getBytes());
                    // leaving this out for now since not all requests require data output
                    // out.write(data);
                    out.flush();
                } 
                else    
                {
                    String error = ""; // leaving blank need to figure out dynamic responses
                    out.write(error.getBytes());
                    out.flush();
                }
            }
            else    // invalid will result in a 400
            {
                String error = ""; // leaving blank need to figure out dynamic responses
                out.write(error.getBytes());
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
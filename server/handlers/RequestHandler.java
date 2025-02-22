package server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import server.requests.HttpRequestFormat;
import server.requests.HttpRequestHeaders;
import server.requests.HttpRequestLine;

public class RequestHandler 
{
    // this is for when we want the server to read the info
    public static HttpRequestFormat parse(BufferedReader reader) throws IOException 
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

    // gonna try and implement using comment logic for parse() in HttpRequestFormat.java WORK IN PROGRESS
    public static void handleRequest(Socket clientSocket)
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
}

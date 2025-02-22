package server.requests;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestFormat {
    private HttpRequestLine requestLine;

    private HttpRequestHeaders headers;

    // what you send to the server if document is specified (can be optional)
    private String body;

    public HttpRequestFormat() {
        this(new HttpRequestLine("GET", "/"), new HttpRequestHeaders(), "");
    }

    public HttpRequestFormat(HttpRequestLine requestLine, HttpRequestHeaders headers) {
        this(requestLine, headers, "");
    }

    public HttpRequestFormat(HttpRequestLine requestLine, HttpRequestHeaders headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = (body != null)? body : "";
    }

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

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpRequestHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setRequestLine(HttpRequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public void setHeaders(HttpRequestHeaders headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // return the http formatted string (idk if this necessary) - Itll prob work for debug and tests - Ulices
    @Override
    public String toString() {
        return requestLine.toString() + "\r\n" + 
                headers.toString() + "\r\n" +
                ((body.isEmpty())? "" : body);
    }

    // dummy test to see HttpRequestFormat works. See HttpRequestFileReader.java for a dynamic example
    public static void main(String[] args) {
        HttpRequestLine requestLine = new HttpRequestLine("GET", "/data/test.txt");
        HttpRequestHeaders headers = HttpRequestHeaders.createHeader("localhost:9999", "text/html", 100);
        String body = "I am a piece of text.\r\n\r\nIf you see this, this means it worked.";
        HttpRequestFormat request = new HttpRequestFormat(requestLine, headers, body);
        System.out.println(request);
    }
}
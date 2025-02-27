package server.responses;

public enum ResponseHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    DATE("Date"),
    SERVER("Server"),
    CONNECTION("Connection"),
    LAST_MODIFIED("Last-Modified"),
    CACHE_CONTROL("Cache-Control"),
    LOCATION("Location"),
    ALLOW("Allow"),
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    EXPIRES("Expires"),
    CONTENT_TRANSFER_ENCODING("Content-Transfer-Encoding"),
    WWW_AUTHENTICATE("WWW-Authenticate"), 
    WARNING("Warning"), 
    ACCEPT_RANGE("Accept-Range");

    private final String header;

    ResponseHeader(String header) {
        this.header = header;
    }

    public String getHeaderName() {
        return header;
    }
}

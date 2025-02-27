package server.responses;

public enum ResponseCode {
    OK(200, "OK"), CREATED(201, "Created"), NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"), UNAUTHORIZED(401, "Unauthorized"), 
    FORBIDDEN(403, "Forbidden"), NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusNumber;
    private final String statusResponse;
    
    ResponseCode(int statusNumber, String statusResponse) {
        this.statusNumber = statusNumber;
        this.statusResponse = statusResponse;
    }

    public String getStatus() {
        return statusNumber + " " + statusResponse;
    }
}

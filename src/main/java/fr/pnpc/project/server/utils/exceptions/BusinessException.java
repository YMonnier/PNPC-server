package fr.pnpc.project.server.utils.exceptions;

import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.ws.rs.core.Response;

@Data
public class BusinessException extends Exception {
    /**
     * contains redundantly the HTTP status of the response sent back to the client in case of error, so that
     * the developer does not have to look into the response headers. If null a default
     */
    @Expose
    private Response.Status status;

    /**
     * detailed error description for developers
     */
    @Expose
    private String developerMessage;

    /**
     * simple message of the error.
     */
    @Expose
    private String message;

    /**
     * BusinessEception Constructor
     *
     * @param status,           HTTP Status of the response.
     * @param message,          exception message.
     * @param developerMessage, detailed error description for developers.
     */
    public BusinessException(Response.Status status,
                             String message,
                             String developerMessage) {
        super(message);
        this.message = message;
        this.status = status;
        this.developerMessage = developerMessage;
    }

    public BusinessException(Throwable e) {
        this(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), null);
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }

}
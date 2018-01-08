package fr.pnpc.project.server.utils.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by bruno on 15/02/15.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {

        BusinessException errorMessage = new BusinessException(ex);
        setHttpStatus(ex, errorMessage);
        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        errorMessage.setDeveloperMessage(errorStackTrace.toString());
        errorMessage.setLink(null);

        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void setHttpStatus(Throwable ex, BusinessException businessException) {
        if(ex instanceof WebApplicationException) {
            businessException.setStatus(Response.Status.fromStatusCode(((WebApplicationException) ex).getResponse().getStatus()));
        } else {
            businessException.setStatus(Response.Status.INTERNAL_SERVER_ERROR); //defaults to internal server error 500
        }
    }
}
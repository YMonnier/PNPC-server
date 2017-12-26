package fr.pnpc.project.server.utils.errors;

import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by stephen on 14/10/17.
 */

@Data
@XmlRootElement
public class Error {


    private int status;

    private Collection<String> errors;

    private Error(Builder builder) {
        this.status = builder.status;
        this.errors = builder.errors;
    }

    @XmlElement
    public int getStatus() {
        return status;
    }

    @XmlElement
    public Collection<String> getErrors() {
        return errors;
    }

    public Response getResponse() {
        return Response
                .status(this.status)
                .entity(this)
                .build();
    }

    public static Error badRequest(String... errors) {
        return new Builder()
                .setStatus(Response.Status.BAD_REQUEST.getStatusCode())
                .setErrors(errors)
                .build();
    }

    public static Error badRequest(List<String> list) {
        return new Builder()
                .setStatus(Response.Status.BAD_REQUEST.getStatusCode())
                .setErrors(list)
                .build();
    }

    public static Error notFound(String identifier) {
        return new Builder()
                .setErrors("The resources " + identifier + " does not exist.")
                .setStatus(Response.Status.NOT_FOUND.getStatusCode())
                .build();
    }

    public static Error internalServer(Exception exception) {
        return new Builder()
                .setErrors(exception.getLocalizedMessage())
                .setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .build();
    }

    public static Error unauthorized() {
        return new Builder()
                .setStatus(Response.Status.UNAUTHORIZED.getStatusCode())
                .setErrors("Need authentication")
                .build();
    }

    public static class Builder {

        private int status;

        private Collection<String> errors;

        public Error build() {
            return new Error(this);
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setErrors(Set<ConstraintViolation<Object>> errors) {
            if (this.errors == null && errors != null) {
                this.errors = new ArrayList<>();
                this.errors.addAll(errors.stream()
                        .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                        .collect(Collectors.toList()));
            }
            return this;
        }

        public Builder setErrors(String... errors) {
            if (this.errors == null && errors != null) {
                this.errors = new ArrayList<>();
                Collections.addAll(this.errors, errors);
            }
            return this;
        }

        public Builder setErrors(List<String> list) {
            if (this.errors == null && list != null) {
                this.errors = list;
            }
            return this;
        }
    }
}
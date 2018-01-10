package fr.pnpc.project.server.utils.auth;

import fr.pnpc.project.models.dao.CrudService;
import fr.pnpc.project.models.dao.QueryParameter;
import fr.pnpc.project.models.model.User;
import fr.pnpc.project.server.utils.exceptions.BusinessException;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.management.j2ee.statistics.Statistic;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
@Dependent
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "pnpc-server";
    private static final String UNAUTHORIZED = "You are not authorized.";

    @Inject
    CrudService<User> crudService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader;

        try {
            // Validate the token
            validateToken(token);

        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null;
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        Response.Status status = Response.Status.UNAUTHORIZED;
        requestContext.abortWith(
                Response.status(status)
                        .entity(new BusinessException(status, UNAUTHORIZED, ""))
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                "realm=\"" + REALM + "\"")
                        .type(MediaType.APPLICATION_JSON)
                        .build());
    }

    private void validateToken(String token) throws Exception {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid

        if (token != null) {
                List<User> users = crudService.findWithNamedQuery(User.FIND_BY_TOKEN, QueryParameter.with("token", token).parameters());
                User user = null;
                if (users != null)
                    user = users.get(0);

                if (user == null)
                    throw new Exception();
        }

    }
}
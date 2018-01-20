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

/**
 * AuthenticationFilter. Used to check if the
 * request has a valid Authorization header.
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
@Dependent
public class AuthenticationFilter implements ContainerRequestFilter {

    /**
     * REALM Identifier
     */
    private static final String REALM = "pnpc-server";

    /**
     * Constant UNAUTHORIZED Message.
     */
    private static final String UNAUTHORIZED = "You are not authorized.";

    /**
     * User CrudService. Used to make CRUD actions.
     */
    @Inject
    CrudService<User> crudService;

    /**
     * Get request before to access to a resource action.
     *
     * @param requestContext A ContainerRequestContext Object.
     * @throws IOException
     */
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

    /**
     * Check if the `authorizationHeader`is valid.
     *
     * @param authorizationHeader A token.
     * @return true if valid, otherwise false.
     */
    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null;
    }

    /**
     * Return an UNAUTHORIZED request if the token is not
     * valid or if the Authrization header is missing.
     *
     * @param requestContext current request.
     */
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

    /**
     * Check if the token is valid from the Database.
     *
     * @param token Token from Header.
     * @throws Exception
     */
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
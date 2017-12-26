package fr.pnpc.project.server.auth;

import fr.pnpc.project.models.dao.CrudService;
import fr.pnpc.project.models.dao.QueryParameter;
import fr.pnpc.project.models.model.User;
import fr.pnpc.project.server.utils.errors.Error;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by stephen on 14/10/17.
 */
public abstract class AuthorizationService {

    @Inject
    CrudService<User> crudService;

    protected boolean authorization;
    protected User authenticate;

    public AuthorizationService(@Context HttpHeaders headers) {
        authorization = false;

        String token = headers.getRequestHeader("Authorization").get(0);
        //getHeaderString("Authorization");
        if (token != null) {
            try {

                List<User> users = crudService.findWithNamedQuery("GET_BY_TOKEN", QueryParameter.with("token", token).parameters());
                if(users != null)
                    authenticate = users.get(0);

                if (authenticate != null)
                    authorization = true;
            } catch (Exception e) {
            }
        }
    }

    public AuthorizationService() {
    }

    protected Response getUnauthorization() {
        return Error.unauthorized().getResponse();
    }
}
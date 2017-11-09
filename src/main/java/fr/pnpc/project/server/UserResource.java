package fr.pnpc.project.server;

import fr.pnpc.project.models.User;
import fr.pnpc.project.models.dao.CrudServiceBean;
import fr.pnpc.project.models.ejb.StringSingleton;
import fr.pnpc.project.models.ejb.UserManager;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/users")
@Stateless
public class UserResource {
    @EJB
    StringSingleton csb;
    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt() {
        return csb.getJsonString();
    }
}

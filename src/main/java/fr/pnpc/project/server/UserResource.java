package fr.pnpc.project.server;

import fr.pnpc.project.models.ejb.UserManager;
import fr.pnpc.project.models.exceptions.NotValidException;
import fr.pnpc.project.models.exceptions.NullObjectException;
import fr.pnpc.project.models.model.User;

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
    UserManager userManager;
    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        User user = new User.Builder()
                .setEmail("ysee@test.com")
                .setNickname("Wow.....")
                .setPassword("OKOKOKOKOKOK")
                .setPhoneNumber("0651576906")
                .build();
        try {
            user = userManager.register(user);
        } catch (NotValidException | NullObjectException e) {
            return e.toString();
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
        return user.toString();
    }
}

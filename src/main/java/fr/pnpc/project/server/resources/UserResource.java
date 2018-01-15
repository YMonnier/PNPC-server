package fr.pnpc.project.server.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.pnpc.project.models.ejb.PassageManager;
import fr.pnpc.project.models.ejb.UserManager;
import fr.pnpc.project.models.ejb.WaypointManager;
import fr.pnpc.project.models.exceptions.LoginNotAllowException;
import fr.pnpc.project.models.exceptions.NotFoundException;
import fr.pnpc.project.models.exceptions.ObjectNotValidException;
import fr.pnpc.project.models.model.Passage;
import fr.pnpc.project.models.model.User;
import fr.pnpc.project.models.model.Waypoint;
import fr.pnpc.project.server.utils.auth.Secured;
import fr.pnpc.project.server.utils.auth.Util;
import fr.pnpc.project.server.utils.exceptions.BusinessException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/users")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserManager userManager;

    @Inject
    PassageManager passageManager;

    @Inject
    WaypointManager waypointManager;

    private final static Logger LOGGER = Logger.getLogger(UserResource.class.getSimpleName());

    public UserResource() {
    }

    /**
     * Method handling HTTP POST requests. The returned object will be sent
     * to the client as JSON Object.
     *
     * @param user
     * @return
     */
    @Path("register/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User create(User user) throws BusinessException {
        LOGGER.log(Level.INFO, "#POST %s", user.toString());

        User u = null;
        try {
            u = userManager.register(user);
        } catch (Exception e) {
            throw new BusinessException(Response.Status.BAD_REQUEST,
                    e.getMessage(), Util.stackTraceToString(e));

        }
        return u;
    }

    /**
     * Method handling HTTP POST requests. The returned object will be sent
     * to the client as JSON Object.
     *
     * @return JSON Object containing user information.
     */
    @Path("login/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public User login(String json) throws BusinessException {
        LOGGER.log(Level.INFO, "#POST %s", json);

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        String nickname = jsonObject.get("nickname").getAsString();
        String password = jsonObject.get("mdp").getAsString();


        User user = null;
        try {
            user = userManager.login(nickname, password);
        } catch (ObjectNotValidException e) {
            throw new BusinessException(Response.Status.BAD_REQUEST,
                    e.getMessage(), Util.stackTraceToString(e));
        } catch (NotFoundException e) {
            throw new BusinessException(Response.Status.NOT_FOUND,
                    e.getMessage(), Util.stackTraceToString(e));
        } catch (LoginNotAllowException e) {
            throw new BusinessException(Response.Status.UNAUTHORIZED,
                    e.getMessage(), Util.stackTraceToString(e));
        } catch (Exception e) {
            throw new BusinessException(Response.Status.BAD_REQUEST,
                    e.getMessage(), "Error JWT.");
        }
        return user;
    }

    @Path("/{user_id}/passages/{waypoint_id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    public Passage createPassage(@PathParam("user_id") long userId, @PathParam("waypoint_id") long waypointId) throws BusinessException {
        LOGGER.log(Level.INFO, "#POST userId %d waypointId %d", new Object[]{userId, waypointId});

        Passage passage = null;

        try {
            User u = userManager.getById(userId);
            Waypoint w = waypointManager.getById(waypointId);

            passage = new Passage(u, w);
            passage  = passageManager.create(passage);
        } catch (NotFoundException e) {
            throw new BusinessException(Response.Status.NOT_FOUND,
                    e.getMessage(), Util.stackTraceToString(e));
        } catch (ObjectNotValidException e) {
            throw new BusinessException(Response.Status.BAD_REQUEST, e.getMessage(), Util.stackTraceToString(e));
        }

        return passage;
    }

    @Path("/{user_id}/passages")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public List<Passage> getAllPassagesByUserId(@PathParam("user_id") long id) throws BusinessException {
        LOGGER.log(Level.INFO,"#GET %d", id);
        List<Passage> passages = null;
        try {
            passages = passageManager.getPassagesByUserId(id);
        } catch (NotFoundException e) {
            throw new BusinessException(Response.Status.NOT_FOUND, e.getMessage(), Util.stackTraceToString(e));
        }

        return passages;
    }

    @Path("/{user_id}/passages/{passage_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Passage getPassage(@PathParam("user_id") int userId, @PathParam("passage_id") int passageId) throws BusinessException {
        LOGGER.log(Level.INFO, "#GET Passage with userId %d and passageId : %d", new Object[]{userId, passageId});

        Passage passage = null;

        try {
            passage = passageManager.getPassage(userId, passageId);
        } catch (NotFoundException e) {
            throw new BusinessException(Response.Status.NOT_FOUND, e.getMessage(), Util.stackTraceToString(e));
        }

        return passage;
    }
}

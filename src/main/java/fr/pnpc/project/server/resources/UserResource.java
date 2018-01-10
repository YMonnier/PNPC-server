package fr.pnpc.project.server.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.pnpc.project.models.ejb.PassageManager;
import fr.pnpc.project.models.ejb.UserManager;
import fr.pnpc.project.models.exceptions.NotValidException;
import fr.pnpc.project.models.exceptions.NullObjectException;
import fr.pnpc.project.models.exceptions.PassageNotExistException;
import fr.pnpc.project.models.model.Passage;
import fr.pnpc.project.models.model.User;
import fr.pnpc.project.models.util.Validator;
import fr.pnpc.project.models.util.ValidatorManager;
import fr.pnpc.project.server.utils.auth.AuthorizationService;
import fr.pnpc.project.server.utils.errors.Error;
import org.mindrot.jbcrypt.BCrypt;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Path("/users")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends AuthorizationService {

    @Inject
    UserManager userManager;

    @Inject
    PassageManager passageManager;

    private final static Logger LOGGER = Logger.getLogger(UserResource.class.getSimpleName());

    @Context
    UriInfo uriInfo;

    private Validator<User> userValidator = new ValidatorManager();
    private Validator<Passage> passageValidator = new ValidatorManager();

    public UserResource(@Context HttpHeaders headers) {
        super(headers);
    }

    public UserResource() {
        super();
    }

    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     *
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "HelloWorld !";
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
    public Response create(User user) throws Exception {
        LOGGER.info("#POST " + user.toString());
        Response response = null;
        Set<ConstraintViolation<User>> constraintViolations = userValidator.constraintViolations(user);

        /*if (constraintViolations.size() > 0) {
            List<String> errors = new ArrayList<>();
            constraintViolations.forEach(u -> errors.add(u.getMessage()));
            Error error= Error.badRequest(errors);
            response = error.getResponse();

            LOGGER.warning("    - Constraints Violation Error" + error.toString());
            LOGGER.warning("    - Constraints Violation messages " + errors.toString());
            LOGGER.warning("    - Constraints Violation response " + response.toString());
        } else {
            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);

            try {
                user = userManager.register(user);

                URI builder = uriInfo.getAbsolutePathBuilder()
                        .build();

                response = Response
                        .created(builder)
                        .entity(user)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.severe("    - Database exception " + response.toString());
                response = Error.internalServer(e).getResponse();
                //TODO : Rollback
            }
        }*/

        user = userManager.register(user);

        URI builder = uriInfo.getAbsolutePathBuilder()
                .build();

        response = Response
                .created(builder)
                .entity(user)
                .build();

        return response;
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
    public Response login(String json) {
        LOGGER.info("#POST " + json.toString());
        Response response = null;
        User user = null;
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();

        if (email == null || password == null) {
            response = Error.badRequest("The nickname or password may not be null.")
                    .getResponse();
            LOGGER.warning("#POST " + response.toString());
        } else {
            try {
                List<User> users = null;
                //userManager.findWithNamedQuery(User.class, "GET_BY_MAIL", QueryParameter.with("email", email).parameters());
                if (users != null)
                    user = users.get(0);

                if (user != null) {
                    if (BCrypt.checkpw(password, user.getPassword())) {

                        //TODO : Set token
                        //String token = TokenUtil.generate(user);
                        //user.setToken(token);

                        response = Response.ok(user).build();

                        //crudService.update(user);
                        //crudService.commit();

                        URI builderUri = uriInfo.getAbsolutePathBuilder()
                                .build();

                        response = Response
                                .created(builderUri)
                                .entity(user)
                                .build();
                        LOGGER.info("#POST " + response.toString());

                    } else {
                        response = Error.badRequest("Bad password...")
                                .getResponse();
                        LOGGER.warning("#POST " + response.toString());
                    }
                } else {
                    response = Error.badRequest("Email does not exist.")
                            .getResponse();
                    LOGGER.warning("#POST " + response.toString());
                }
            } catch (Exception exception) {
                LOGGER.warning("#POST " + exception.getLocalizedMessage());
                response = Error.internalServer(exception)
                        .getResponse();
                //TODO : Rollback
            }
        }

        return response;
    }

    @Path("/{user_id}/passages")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPassage(Passage passage){
        LOGGER.info("#POST " + passage.toString());
        Response response;
        Set<ConstraintViolation<Passage>> constraintViolations = passageValidator.constraintViolations(passage);

        if(this.authorization){
            if (constraintViolations.size() > 0) {
                response = Error.badRequest(constraintViolations.toString())
                        .getResponse();
                LOGGER.warning("#POST " + response.toString());
            }
            else{
                try {
                    passage = passageManager.create(passage);

                    URI builder = uriInfo.getAbsolutePathBuilder()
                            .build();

                    response = Response
                            .created(builder)
                            .entity(passage)
                            .build();

                } catch (NullObjectException e) {
                    LOGGER.warning("#POST " + e.getLocalizedMessage());
                    response = Error.internalServer(e).getResponse();
                    //TODO : Rollback
                } catch (NotValidException e) {
                    LOGGER.warning("#POST " + e.getLocalizedMessage());
                    response = Error.internalServer(e).getResponse();
                    //TODO : Rollback
                }
            }

        }
        else {
            response = this.getUnauthorization();
        }

        return response;
    }

    @Path("/{user_id}/passages")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPassagesByUserId(@PathParam("user_id") int id){
        LOGGER.info("#GET " + id);
        Response response;

        if(this.authorization) {
            try {
                List<Passage> passages = passageManager.getPassagesByUserId(id);

                URI builder = uriInfo.getAbsolutePathBuilder()
                        .build();

                response = Response
                        .created(builder)
                        .entity(passages)
                        .build();

                } catch (PassageNotExistException e) {
                    LOGGER.warning("#GET " + e.getLocalizedMessage());
                    response = Error.internalServer(e).getResponse();
                    //TODO : Rollback
                }
        }
        else{
            response = this.getUnauthorization();
        }

        return response;
    }

    @Path("/{user_id}/passages/{passage_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPassage(@PathParam("user_id") int userId, @PathParam("passage_id") int passageId){
        LOGGER.info("#GET Passage with userId " + userId + " and passageId : " + passageId);
        Response response;

        if(this.authorization){
            try {
                Passage passage = passageManager.getPassage(userId, passageId);

                URI builder = uriInfo.getAbsolutePathBuilder()
                        .build();

                response = Response
                        .created(builder)
                        .entity(passage)
                        .build();

            } catch (PassageNotExistException e) {
                LOGGER.warning("#GET " + e.getLocalizedMessage());
                response = Error.internalServer(e).getResponse();
                //TODO : Rollback
            }
        }
        else{
            response = this.getUnauthorization();
        }

        return response;
    }
}

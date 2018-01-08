package fr.pnpc.project.server.resources;

import fr.pnpc.project.models.ejb.WaypointManager;
import fr.pnpc.project.models.exceptions.NotValidException;
import fr.pnpc.project.models.exceptions.NullObjectException;
import fr.pnpc.project.models.model.Waypoint;
import fr.pnpc.project.models.util.Validator;
import fr.pnpc.project.models.util.ValidatorManager;
import fr.pnpc.project.server.utils.auth.AuthorizationService;
import fr.pnpc.project.server.utils.errors.Error;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Set;
import java.util.logging.Logger;

@Path("/waypoints")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class WaypointResource extends AuthorizationService {

    @Inject
    WaypointManager waypointManager;

    private final static Logger LOGGER = Logger.getLogger(UserResource.class.getSimpleName());

    @Context
    UriInfo uriInfo;

    private Validator<Waypoint> validator = new ValidatorManager();

    public WaypointResource() {
        super();
    }

    public WaypointResource(@Context HttpHeaders headers) {
        super(headers);
    }

    /**
     * Method handling HTTP POST requests. The returned object will be sent
     * to the client as JSON Object.
     *
     * @param waypoint
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Waypoint waypoint){
        LOGGER.info("#POST " + waypoint.toString());
        Response response = null;
        Set<ConstraintViolation<Waypoint>> constraintViolations = validator.constraintViolations(waypoint);

        if(this.authorization) {
            if (constraintViolations.size() > 0) {
                response = Error.badRequest(constraintViolations.toString())
                        .getResponse();
                LOGGER.warning("#POST " + response.toString());
            } else {
                try {
                    waypoint = waypointManager.create(waypoint);

                    URI builder = uriInfo.getAbsolutePathBuilder()
                            .build();

                    response = Response
                            .created(builder)
                            .entity(waypoint)
                            .build();

                } catch (Exception e) {
                    LOGGER.warning("#POST " + e.getLocalizedMessage());
                    response = Error.internalServer(e).getResponse();
                    //TODO : Rollback
                }
            }

        } else {
            response = this.getUnauthorization();
        }

        return response;

    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") int id){
        LOGGER.info("#GET waypoint : " + id);
        Response response = null;

        if(this.authorization){
            Waypoint waypoint = waypointManager.getById(id);

            URI builder = uriInfo.getAbsolutePathBuilder()
                    .build();

            response = Response
                    .created(builder)
                    .entity(waypoint)
                    .build();
        }
        else{
            response = this.getUnauthorization();
        }

        return response;
    }

    @Path("/{id}")
    @DELETE
    public Response delete(@PathParam("id") int id){
        LOGGER.info("#DELETE waypoint : " + id);

        Response response;

        if(this.authorization){
            waypointManager.delete(id);
            response = Response.ok().build();
        }
        else{
            response = this.getUnauthorization();
        }

        return response;
    }

    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(@PathParam("id") int id, Waypoint waypoint){
        LOGGER.info("#PUT waypoint : " + id);
        Response response = null;

        if(this.authorization){
            Set<ConstraintViolation<Waypoint>> constraintViolations = validator.constraintViolations(waypoint);

            if (constraintViolations.size() > 0) {
                response = Error.badRequest(constraintViolations.toString())
                        .getResponse();
                LOGGER.warning("#POST " + response.toString());
            }
            else {
                try {
                    waypoint = waypointManager.update(waypoint);

                    URI builder = uriInfo.getAbsolutePathBuilder()
                            .build();

                    response = Response
                            .created(builder)
                            .entity(waypoint)
                            .build();
                } catch (NullObjectException e) {
                    LOGGER.warning("#PUT " + e.getLocalizedMessage());
                    response = Error.internalServer(e).getResponse();
                } catch (NotValidException e) {
                    LOGGER.warning("#PUT " + e.getLocalizedMessage());
                    response = Error.internalServer(e).getResponse();
                }

            }
        }
        else{
            response = this.getUnauthorization();
        }

        return response;
    }


}

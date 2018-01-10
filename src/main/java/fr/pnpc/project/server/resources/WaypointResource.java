package fr.pnpc.project.server.resources;

import fr.pnpc.project.models.ejb.WaypointManager;
import fr.pnpc.project.models.exceptions.NotFoundException;
import fr.pnpc.project.models.exceptions.ObjectNotValidException;
import fr.pnpc.project.models.model.Waypoint;
import fr.pnpc.project.models.util.Validator;
import fr.pnpc.project.models.util.ValidatorManager;
import fr.pnpc.project.server.utils.auth.Secured;
import fr.pnpc.project.server.utils.auth.Util;
import fr.pnpc.project.server.utils.exceptions.BusinessException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Logger;

@Path("/waypoints")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class WaypointResource {

    @Inject
    WaypointManager waypointManager;

    private final static Logger LOGGER = Logger.getLogger(UserResource.class.getSimpleName());


    public WaypointResource() {
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
    @Secured
    public Waypoint create(Waypoint waypoint) throws BusinessException {
        LOGGER.info("#POST " + waypoint.toString());
        Waypoint w = null;

        try {
            w = waypointManager.create(waypoint);
        } catch (ObjectNotValidException e) {
            throw new BusinessException(Response.Status.BAD_REQUEST, e.getMessage(), Util.stackTraceToString(e));
        }

        return w;

    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    public Waypoint get(@PathParam("id") int id) throws BusinessException {
        LOGGER.info("#GET waypoint : " + id);
        Waypoint waypoint = null;

        try {
            waypoint = waypointManager.getById(id);
        } catch (NotFoundException e) {
            throw new BusinessException(Response.Status.NOT_FOUND,
                    e.getMessage(), Util.stackTraceToString(e));
        }

        return waypoint;
    }

    @Path("/{id}")
    @DELETE
    @Secured
    public void delete(@PathParam("id") int id) throws BusinessException {
        LOGGER.info("#DELETE waypoint : " + id);

        Response response;
        try {
            waypointManager.delete(id);
        } catch (NotFoundException e) {
            throw new BusinessException(Response.Status.NOT_FOUND,
                    e.getMessage(), Util.stackTraceToString(e));
        }
    }

    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    public Waypoint put(@PathParam("id") int id, Waypoint waypoint) throws BusinessException {
        LOGGER.info("#PUT waypoint : " + id);
        Waypoint w = null;

        try {
            w = waypointManager.update(waypoint);
        } catch (ObjectNotValidException e) {
            throw new BusinessException(Response.Status.BAD_REQUEST,
                    e.getMessage(), Util.stackTraceToString(e));
        }

        return w;
    }


}

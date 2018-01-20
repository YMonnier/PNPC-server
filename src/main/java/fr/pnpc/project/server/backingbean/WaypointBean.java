package fr.pnpc.project.server.backingbean;


import fr.pnpc.project.models.ejb.WaypointManager;
import fr.pnpc.project.models.exceptions.ObjectNotValidException;
import fr.pnpc.project.models.model.Waypoint;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
/**
 * `WaypointBean` from backingbean package. Manage the Login view as controller.
 */
public class WaypointBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * A Logger object to log messages.
     */
    private final static Logger LOGGER = Logger.getLogger(WaypointBean.class.getSimpleName());

    /**
     * A Waypoint Object.
     */
    private Waypoint waypoint;

    /**
     * A WaypointManager Object to manage Waypoint type. (CRUD).
     */
    @Inject
    WaypointManager waypointManager;

    /**
     * Default LoginBean constructor. Necessary for `JEE`.
     */
    public WaypointBean() {
        this.waypoint = new Waypoint();
    }

    /**
     * Creates a new Waypoint from JSF input fields.
     *
     * @return A message.
     */
    public String createWaypoint() {
        try {
            waypointManager.create(waypoint);
        } catch (ObjectNotValidException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage());
        }

        return "welcome";
    }

    /**
     * Get all Waypoints from the WaypointManager
     *
     * @return A list of Waypoint object.
     */
    public List getAllWaypoints() {
        return waypointManager.getAll();
    }

    /**
     * Returns the current waypoint.
     *
     * @return A Waypoint.
     */
    public Waypoint getWaypoint() {
        return waypoint;
    }

    /**
     * Sets a Waypoint
     *
     * @param waypoint, new waypoint.
     */
    public void setWaypoint(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}

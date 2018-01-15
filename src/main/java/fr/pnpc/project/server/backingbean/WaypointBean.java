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
public class WaypointBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(WaypointBean.class.getSimpleName());


    private Waypoint waypoint;

    @Inject
    WaypointManager waypointManager;

    public WaypointBean() {
        this.waypoint = new Waypoint();
    }

    public String createWaypoint() {
        try {
            waypointManager.create(waypoint);
        } catch (ObjectNotValidException e) {
            LOGGER.log(Level.INFO, e.getLocalizedMessage());
        }

        return "welcome";
    }

    public List getAllWaypoints() {
        return waypointManager.getAll();
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}

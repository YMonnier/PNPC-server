package fr.pnpc.project.server.backingbean;


import fr.pnpc.project.models.ejb.WaypointManager;
import fr.pnpc.project.models.exceptions.ObjectNotValidException;
import fr.pnpc.project.models.model.Waypoint;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class WaypointBean implements Serializable {

    private static final long serialVersionUID = 1L;

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
            e.printStackTrace();
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

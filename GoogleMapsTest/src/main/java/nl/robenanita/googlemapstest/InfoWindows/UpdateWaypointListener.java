package nl.robenanita.googlemapstest.InfoWindows;

import nl.robenanita.googlemapstest.Route.Waypoint;

/**
 * Created by Rob Verhoef on 29-8-2017.
 */

public interface UpdateWaypointListener {
    public void OnDeleteWaypoint(Waypoint waypoint);
    public void OnMoveUpWaypoint(Waypoint waypoint);
    public void OnMoveDownWaypoint(Waypoint waypoint);
    public void OnRenameWaypoint(Waypoint waypoint, String newName);
    public void OnCloseWaypointWindow();
}

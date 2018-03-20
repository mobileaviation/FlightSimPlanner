package nl.robenanita.googlemapstest.Route.Json;

import nl.robenanita.googlemapstest.Route.Waypoint;

/**
 * Created by Rob Verhoef on 18-3-2018.
 */

public class WaypointObject {
    public WaypointObject(Waypoint waypoint)
    {
        _waypoint = waypoint;
    }

    private Waypoint _waypoint;

    public String getName() { return _waypoint.name; }
    public Double getLatitude() {return _waypoint.location.getLatitude();}
    public Double getLongitude() {return _waypoint.location.getLongitude();}
    public String getType() {return _waypoint.waypointType.toString();}
}

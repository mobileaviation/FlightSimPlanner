package nl.robenanita.googlemapstest.Route.Json;

import nl.robenanita.googlemapstest.Route.Waypoint;

/**
 * Created by Rob Verhoef on 18-3-2018.
 */

public class WaypointObject {
    public WaypointObject(Waypoint waypoint)
    {
        _waypoint = waypoint;
        assign();
    }

    private transient Waypoint _waypoint;

    private void assign()
    {
        name = _waypoint.name;
        latitude = _waypoint.location.getLatitude();
        longitude = _waypoint.location.getLongitude();
        type = _waypoint.waypointType.toString();
    }

    private String name;
    private Double latitude;
    private Double longitude;
    private String type;
}

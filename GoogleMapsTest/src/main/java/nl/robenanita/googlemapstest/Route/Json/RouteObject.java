package nl.robenanita.googlemapstest.Route.Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Route.Waypoint;

/**
 * Created by Rob Verhoef on 18-3-2018.
 */

public class RouteObject {
    public RouteObject(Route route)
    {
        _route = route;
        assign();
    }
    private transient Route _route;
    private String name;
    private Integer altitude;
    private AirportObject departure;
    private AirportObject destination;
    private AirportObject alternate;
    private WaypointObject[] waypoints;

    private void assign()
    {
        name = _route.name;
        altitude = _route.altitude;
        departure = new AirportObject(_route.departure_airport);
        destination = new AirportObject(_route.destination_airport);
        alternate = new AirportObject(_route.alternate_airport);

        assignWaypoints();
    }

    private void assignWaypoints()
    {
        waypoints = new WaypointObject[_route.Waypoints.size()];
        int i = 0;
        for (Waypoint waypoint : _route.Waypoints)
        {
            waypoints[i] = new WaypointObject(waypoint);
            i++;
        }
    }

    public String toJson()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();;
        return gson.toJson(this);
    }
}

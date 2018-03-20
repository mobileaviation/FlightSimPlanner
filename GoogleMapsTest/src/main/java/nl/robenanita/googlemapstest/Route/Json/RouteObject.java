package nl.robenanita.googlemapstest.Route.Json;

import org.json.JSONException;
import org.json.JSONObject;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Route.Route;

/**
 * Created by Rob Verhoef on 18-3-2018.
 */

public class RouteObject {
    public RouteObject(Route route)
    {
        _route = route;
    }
    private Route _route;

    public String getName() { return _route.name; }
    public AirportObject getDeparture() {return new AirportObject(_route.departure_airport);}
    public AirportObject getDestination() {return new AirportObject(_route.destination_airport);}
    public AirportObject getAlternate() {return new AirportObject(_route.alternate_airport);}
    public Integer getAltitude() {return _route.altitude;}

    public JSONObject SerializeJson()
    {
        try {
            return new JSONObject().put("route", this);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

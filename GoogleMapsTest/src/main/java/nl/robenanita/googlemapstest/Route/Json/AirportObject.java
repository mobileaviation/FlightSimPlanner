package nl.robenanita.googlemapstest.Route.Json;

import nl.robenanita.googlemapstest.Airport.Airport;

/**
 * Created by Rob Verhoef on 18-3-2018.
 */

public class AirportObject {
    public AirportObject(Airport airport)
    {
        _airport = airport;
    }
    private Airport _airport;

    public String getName() {return _airport.name;}
    public String getIdent() {return _airport.ident;}
    public Double getLatitude() {return _airport.latitude_deg;}
    public Double getLongitude() {return _airport.longitude_deg;}
    public String getCountry() {return _airport.iso_country;}
}

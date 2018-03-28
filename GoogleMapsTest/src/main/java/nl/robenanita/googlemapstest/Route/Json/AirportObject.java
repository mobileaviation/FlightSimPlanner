package nl.robenanita.googlemapstest.Route.Json;

import java.sql.Struct;

import nl.robenanita.googlemapstest.Airport.Airport;

/**
 * Created by Rob Verhoef on 18-3-2018.
 */

public class AirportObject {
    public AirportObject(Airport airport)
    {
        _airport = airport;
        assign();
    }
    private transient Airport _airport;
    private String name;
    private String ident;
    private Double latitude;
    private Double longitude;
    private String country;

    private void assign()
    {
        name = _airport.name;
        ident = _airport.ident;
        latitude = _airport.latitude_deg;
        longitude = _airport.longitude_deg;
        country = _airport.iso_country;
    }
}

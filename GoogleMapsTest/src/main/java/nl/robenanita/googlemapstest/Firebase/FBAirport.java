package nl.robenanita.googlemapstest.Firebase;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Airport.AirportType;

public class FBAirport {
    public FBAirport()
    {
        runways = new ArrayList<>();
    }

    public Integer index;
    public Integer id;
    public String ident;
    public AirportType type;
    public String name;
    public Double latitude_deg;
    public Double longitude_deg;
    public Double elevation_ft;
    public String continent;
    public String iso_country;
    public String iso_region;
    public String municipality;
    public String scheduled_service;
    public String gps_code;
    public String iata_code;
    public String local_code;
    public String home_link;
    public String wikipedia_link;
    public String keywords;
    public InternalError version;
    public double heading;

    public ArrayList<FBRunway> runways;
    public ArrayList<FBFrequency> frequencies;
}

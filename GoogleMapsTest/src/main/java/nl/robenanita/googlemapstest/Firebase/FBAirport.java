package nl.robenanita.googlemapstest.Firebase;
import android.content.ContentValues;

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

    public ArrayList<FBRunway> runways;
    public ArrayList<FBFrequency> frequencies;

    public ContentValues getAirportContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(FBDBHelper.C_id, id);
        values.put(FBDBHelper.C_ident, ident);
        values.put(FBDBHelper.C_continent, continent);
        values.put(FBDBHelper.C_elevation_ft, elevation_ft);
        values.put(FBDBHelper.C_gps_code, gps_code);
        values.put(FBDBHelper.C_home_link, home_link);
        values.put(FBDBHelper.C_iata_code, iata_code);
        values.put(FBDBHelper.C_iso_country, iso_country);
        values.put(FBDBHelper.C_iso_region, iso_region);
        values.put(FBDBHelper.C_keywords, keywords);
        values.put(FBDBHelper.C_latitude_deg, latitude_deg);
        values.put(FBDBHelper.C_local_code, local_code);
        values.put(FBDBHelper.C_longitude_deg, longitude_deg);
        values.put(FBDBHelper.C_municipality, municipality);
        values.put(FBDBHelper.C_name, name);
        values.put(FBDBHelper.C_scheduled_service, scheduled_service);
        values.put(FBDBHelper.C_type, type.toString());
        values.put(FBDBHelper.C_wikipedia_link, wikipedia_link);
        return values;
    }

}

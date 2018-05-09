package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;

public class FBRunway {
    public FBRunway(){}

    public Integer id;
    public Integer airport_ref;
    public String airport_ident;
    public Integer length_ft;
    public Integer width_ft;
    public String surface;
    public String le_ident;
    public Double le_latitude_deg;
    public Double le_longitude_deg;
    public Integer le_elevation_ft;
    public Double le_heading_degT;
    public Integer le_displaced_threshold_ft;
    public String he_ident;
    public Double he_latitude_deg;
    public Double he_longitude_deg;
    public Integer he_elevation_ft;
    public Double he_heading_degT;
    public Integer he_displaced_threshold_ft;
    public Integer lighted;
    public Integer closed;

    public ContentValues getRunwayValues()
    {
        ContentValues values = new ContentValues();
        values.put(FBDBHelper.C_id, id);
        values.put(FBDBHelper.C_airport_ref, airport_ref);
        values.put(FBDBHelper.C_airport_ident, airport_ident);
        values.put(FBDBHelper.C_length, length_ft);
        values.put(FBDBHelper.C_width, width_ft);
        values.put(FBDBHelper.C_surface, surface);
        values.put(FBDBHelper.C_le_ident, le_ident);
        values.put(FBDBHelper.C_le_latitude, le_latitude_deg);
        values.put(FBDBHelper.C_le_longitude, le_longitude_deg);
        values.put(FBDBHelper.C_le_elevation, le_elevation_ft);
        values.put(FBDBHelper.C_le_heading, le_heading_degT);
        values.put(FBDBHelper.C_le_displaced_threshold, le_displaced_threshold_ft);
        values.put(FBDBHelper.C_he_ident, he_ident);
        values.put(FBDBHelper.C_he_latitude, he_latitude_deg);
        values.put(FBDBHelper.C_he_longitude, he_longitude_deg);
        values.put(FBDBHelper.C_he_elevation, he_elevation_ft);
        values.put(FBDBHelper.C_he_heading, he_heading_degT);
        values.put(FBDBHelper.C_he_displaced_threshold, he_displaced_threshold_ft);
        values.put(FBDBHelper.C_lighted, lighted);
        values.put(FBDBHelper.C_closed, closed);

        return values;
    }
}

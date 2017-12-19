package nl.robenanita.googlemapstest.Airport;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by Rob Verhoef on 24-3-14.
 */
public class Runway implements Serializable {
    public Integer id;
    public Integer airport_ref;
    public String airport_ident;
    public Integer length_ft;
    public Integer width_ft;
    public String surface;
    public String le_ident;
    public double le_latitude_deg;
    public double le_longitude_deg;
    public Integer le_elevation_ft;
    public double le_heading_degT;
    public Integer le_displaced_threshold_ft;
    public String he_ident;
    public double he_latitude_deg;
    public double he_longitude_deg;
    public Integer he_elevation_ft;
    public double he_heading_degT;
    public Integer he_displaced_threshold_ft;
    public String active;
    public Marker hiMarker;
    public Marker lowMarker;
}

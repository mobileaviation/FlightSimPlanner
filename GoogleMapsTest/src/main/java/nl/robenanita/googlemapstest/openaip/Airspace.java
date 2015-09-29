package nl.robenanita.googlemapstest.openaip;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Created by Rob Verhoef on 29-9-2015.
 */
public class Airspace {
    public AirspaceCategory Category;
    public String Version;
    public Integer ID;
    public String Country;
    public String Name;
    public Integer AltLimit_Top;
    public AltitudeReference AltLimit_Top_Ref;
    public Integer AltLimit_Bottom;
    public AltitudeReference AltLimit_Bottom_Ref;
    public Geometry Geometry;
}

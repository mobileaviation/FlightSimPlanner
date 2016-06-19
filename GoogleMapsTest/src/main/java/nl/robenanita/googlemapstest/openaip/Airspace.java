package nl.robenanita.googlemapstest.openaip;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 29-9-2015.
 */
public class Airspace {
    public Airspace()
    {
        coordinates = new ArrayList<Coordinate>();
    }

    public AirspaceCategory Category;
    public String Version;
    public Integer ID;
    public String Country;
    public String Name;
    public Integer AltLimit_Top;
    public AltitudeUnit AltLimit_Top_Unit;
    public AltitudeReference AltLimit_Top_Ref;
    public Integer AltLimit_Bottom;
    public AltitudeUnit AltLimit_Bottom_Unit;
    public AltitudeReference AltLimit_Bottom_Ref;
    public Geometry Geometry;
    public ArrayList<Coordinate> coordinates;
}

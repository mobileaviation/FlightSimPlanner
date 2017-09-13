package nl.robenanita.googlemapstest.Airspaces;

import android.database.Cursor;

import com.google.android.gms.maps.model.Polygon;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKBReader;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class Airspace {
    public Airspace()
    {
        coordinates = new ArrayList<Coordinate>();
        AltLimit_Top = 0;
        AltLimit_Top_Ref = AltitudeReference.STD;
        AltLimit_Top_Unit = AltitudeUnit.F;
        AltLimit_Bottom = 0;
        AltLimit_Bottom_Ref = AltitudeReference.STD;
        AltLimit_Bottom_Unit = AltitudeUnit.F;
    }

    public AirspaceCategory Category;
    public String Version;
    public Integer ID;
    public String Country;
    public String Name;
    public Integer AltLimit_Top;
    public Integer getAltLimit_Top()
    {
        return (AltLimit_Top == null) ? 0 : AltLimit_Top;
    }
    public AltitudeUnit AltLimit_Top_Unit;

    public AltitudeReference AltLimit_Top_Ref;
    public Integer AltLimit_Bottom;
    public Integer getAltLimit_Bottom()
    {
        return (AltLimit_Top == null) ? 0 : AltLimit_Bottom;
    }
    public AltitudeUnit AltLimit_Bottom_Unit;
    public AltitudeReference AltLimit_Bottom_Ref;
    private com.vividsolutions.jts.geom.Geometry Geometry;
    public ArrayList<Coordinate> coordinates;

    public Polygon airspacePolygon;

    public com.vividsolutions.jts.geom.Geometry getGeometry()
    {
        if (Geometry == null) {
            Coordinate[] c = coordinates.toArray(new Coordinate[coordinates.size()]);

            try {
                Geometry = new GeometryFactory().createPolygon(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Geometry;
    }

    public com.vividsolutions.jts.geom.Geometry getEnvelope()
    {
        if (Geometry == null) {
            Coordinate[] c = coordinates.toArray(new Coordinate[coordinates.size()]);
            Geometry = new GeometryFactory().createPolygon(c);
        }
        return Geometry.getEnvelope();
    }

    public void setGeometry(Geometry geometry)
    {
        this.Geometry = geometry;
        coordinates = new ArrayList<Coordinate>(Arrays.asList(geometry.getCoordinates()));
    }

    public void AssignFromCursor(Cursor cursor)
    {
        try {
            ID = cursor.getInt(cursor.getColumnIndex("_id"));
            Name = cursor.getString(cursor.getColumnIndex("name"));
            Version = cursor.getString(cursor.getColumnIndex("version"));
            Category = AirspaceCategory.valueOf(cursor.getString(cursor.getColumnIndex("category")));
            Country = cursor.getString(cursor.getColumnIndex("country"));
            AltLimit_Top = cursor.getInt(cursor.getColumnIndex("altLimit_top"));
            AltLimit_Bottom = cursor.getInt(cursor.getColumnIndex("altLimit_bottom"));
            AltLimit_Bottom_Ref = AltitudeReference.valueOf(cursor.getString(cursor.getColumnIndex("altLimit_bottom_ref")));
            AltLimit_Top_Ref = AltitudeReference.valueOf(cursor.getString(cursor.getColumnIndex("altLimit_top_ref")));
            AltLimit_Bottom_Unit = AltitudeUnit.valueOf(cursor.getString(cursor.getColumnIndex("altLimit_bottom_unit")));
            AltLimit_Top_Unit = AltitudeUnit.valueOf(cursor.getString(cursor.getColumnIndex("altLimit_top_unit")));
            setGeometry( new WKBReader().read(cursor.getBlob(cursor.getColumnIndex("geometry"))) );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public Location[] getAirspaceB3aLocations()
//    {
//        Location[] locations = new Location[coordinates.size()];
//
//        int i=0;
//        for (Coordinate c: coordinates)
//        {
//            locations[i] = new Location();
//            locations[i].latitude = c.y;
//            locations[i].longitude = c.x;
//            i++;
//        }
//
//        return locations;
//    }
}


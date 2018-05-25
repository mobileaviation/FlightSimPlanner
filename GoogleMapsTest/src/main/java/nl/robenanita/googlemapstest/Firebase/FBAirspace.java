package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;
import android.icu.util.ULocale;
import android.util.Log;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;

import java.util.Calendar;

import nl.robenanita.googlemapstest.Airspaces.AirspaceCategory;
import nl.robenanita.googlemapstest.Airspaces.AltitudeReference;
import nl.robenanita.googlemapstest.Airspaces.AltitudeUnit;

public class FBAirspace {

    private static final String TAG = "GooglemapsTest";

    public Integer AltLimit_Bottom;
    public AltitudeReference AltLimit_Bottom_Ref;
    public AltitudeUnit AltLimit_Bottom_Unit;
    public Integer AltLimit_Top;
    public AltitudeReference AltLimit_Top_Ref;
    public AltitudeUnit AltLimit_Top_Unit;
    public AirspaceCategory Category;
    public String Coordinates;
    public String Country;
    public Integer ID;
    public Integer index;
    public String Name;
    public String Version;

    public ContentValues getAirspaceContentValues()
    {
        try {
            ContentValues values = new ContentValues();
            values.put(FBAirspacesDBHelper.C_airspace_id, ID);
            values.put(FBAirspacesDBHelper.C_altLimit_bottom, AltLimit_Bottom);
            values.put(FBAirspacesDBHelper.C_altLimit_top, AltLimit_Top);
            values.put(FBAirspacesDBHelper.C_altLimit_bottom_ref, AltLimit_Bottom_Ref.toString());
            values.put(FBAirspacesDBHelper.C_altLimit_top_ref, AltLimit_Top_Ref.toString());
            values.put(FBAirspacesDBHelper.C_altLimit_top_unit, AltLimit_Top_Unit.toString());
            values.put(FBAirspacesDBHelper.C_altLimit_bottom_unit, AltLimit_Bottom_Unit.toString());
            values.put(FBAirspacesDBHelper.C_category, Category.toString());
            values.put(FBAirspacesDBHelper.C_country, Country);
            values.put(FBAirspacesDBHelper.C_name, Name);
            values.put(FBAirspacesDBHelper.C_version, Version);

            Geometry g = new WKTReader().read(this.Coordinates);
            Geometry env = g.getEnvelope();
            byte[] coords = new WKBWriter().write(g);
            byte[] envs = new WKBWriter().write(env);

            values.put(FBAirspacesDBHelper.C_geometry, coords);
            values.put(FBAirspacesDBHelper.C_envelope, envs);
            Coordinate[] envCoords = env.getCoordinates();

            values.put(FBAirspacesDBHelper.C_lat_top_left, envCoords[1].y);
            values.put(FBAirspacesDBHelper.C_lon_top_left, envCoords[1].x);
            values.put(FBAirspacesDBHelper.C_lat_bottom_right, envCoords[3].y);
            values.put(FBAirspacesDBHelper.C_lon_bottom_right, envCoords[3].x);

            return values;
        }
        catch (Exception ee)
        {
            Log.e(TAG, "Coordinates reader error: " + ee.getMessage());
            return null;
        }
    }

}

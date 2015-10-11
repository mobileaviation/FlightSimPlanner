package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.WKTWriter;

import nl.robenanita.googlemapstest.openaip.Airspace;
import nl.robenanita.googlemapstest.openaip.Airspaces;

/**
 * Created by Rob Verhoef on 4-10-2015.
 */
public class AirspacesDataSource {
    private SQLiteDatabase database;
    private AirspacesDBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public AirspacesDataSource(Context context) {
        dbHelper = new AirspacesDBHelper(context);
    }

    public void open(){
        try {
            database = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues getAirspaceContentValue(Airspace airspace)
    {
        ContentValues values = new ContentValues();
        values.put(AirspacesDBHelper.C_id, airspace.ID);
        values.put(AirspacesDBHelper.C_altLimit_bottom, airspace.AltLimit_Bottom);
        values.put(AirspacesDBHelper.C_altLimit_top, airspace.AltLimit_Top);
        values.put(AirspacesDBHelper.C_country, airspace.Country);
        values.put(AirspacesDBHelper.C_name, airspace.Name);
        values.put(AirspacesDBHelper.C_version, airspace.Version);
        values.put(AirspacesDBHelper.C_altLimit_bottom_ref, airspace.AltLimit_Bottom_Ref.toString());
        values.put(AirspacesDBHelper.C_altLimit_bottom_unit, airspace.AltLimit_Bottom_Unit.toString());
        values.put(AirspacesDBHelper.C_altLimit_top_ref, airspace.AltLimit_Top_Ref.toString());
        values.put(AirspacesDBHelper.C_altLimit_top_unit, airspace.AltLimit_Top_Unit.toString());
        values.put(AirspacesDBHelper.C_category, airspace.Category.toString());
        WKTWriter writer = new WKTWriter();
        values.put(AirspacesDBHelper.C_geometry, writer.write(airspace.Geometry));
        Envelope envelope = airspace.Geometry.getEnvelopeInternal();
        values.put(AirspacesDBHelper.C_lon_top_left, envelope.getMinX());  // Left Top
        values.put(AirspacesDBHelper.C_lat_top_left, envelope.getMinY());
        values.put(AirspacesDBHelper.C_lot_bottom_right, envelope.getMaxX());  // Right Bottom
        values.put(AirspacesDBHelper.C_lat_bottom_right, envelope.getMaxY());

        return values;
    }

    public void insertAirspace(Airspace airspace)
    {

        try {
            database.insertOrThrow(AirspacesDBHelper.AIRSPACES_TABLE_NAME, null, getAirspaceContentValue(airspace));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Airspaces getAirspacesByPosition(LatLng Position)
    {
        Airspaces airspaces = new Airspaces(null);



        return airspaces;
    }

}

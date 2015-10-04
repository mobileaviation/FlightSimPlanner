package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Bottom);
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Top);
        values.put(AirspacesDBHelper.C_id, airspace.Country);
        values.put(AirspacesDBHelper.C_id, airspace.Country);
        values.put(AirspacesDBHelper.C_id, airspace.Name);
        values.put(AirspacesDBHelper.C_id, airspace.Version);
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Bottom);
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Bottom_Ref.toString());
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Bottom_Unit.toString());
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Top_Ref.toString());
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Top_Unit.toString());
        values.put(AirspacesDBHelper.C_id, airspace.AltLimit_Top);
        values.put(AirspacesDBHelper.C_id, airspace.Category.toString());
        WKTWriter writer = new WKTWriter();
        values.put(AirspacesDBHelper.C_id, writer.write(airspace.Geometry));
        Envelope envelope = airspace.Geometry.getEnvelopeInternal();
        values.put(AirspacesDBHelper.C_lon_top_left, envelope.getMinX());  // Left Top
        values.put(AirspacesDBHelper.C_lat_top_left, envelope.getMinY());
        values.put(AirspacesDBHelper.C_lot_bottom_right, envelope.getMaxX());  // Right Bottom
        values.put(AirspacesDBHelper.C_lat_bottom_right, envelope.getMaxY());

        return values;
    }

    public void insertAirspace(Airspace airspace)
    {
        database.insert(AirspacesDBHelper.AIRSPACES_TABLE, null, getAirspaceContentValue(airspace));
    }

}

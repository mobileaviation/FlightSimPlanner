package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Airport.Runway;
import nl.robenanita.googlemapstest.Airport.RunwaysList;

/**
 * Created by Rob Verhoef on 6-4-14.
 */
public class RunwaysDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public RunwaysDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    private String[] runwayColumns = {
            DBHelper.C_airport_ref,
            DBHelper.C_airport_ident,
            DBHelper.C_length,
            DBHelper.C_width,
            DBHelper.C_surface,
            DBHelper.C_le_ident,
            DBHelper.C_le_latitude,
            DBHelper.C_le_longitude,
            DBHelper.C_le_elevation,
            DBHelper.C_le_heading,
            DBHelper.C_le_displaced_threshold,
            DBHelper.C_he_ident,
            DBHelper.C_he_latitude,
            DBHelper.C_he_longitude,
            DBHelper.C_he_elevation,
            DBHelper.C_he_heading,
            DBHelper.C_he_displaced_threshold
    };

    public void open(){
        try {
            dbHelper.createDataBase();
            database = dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        dbHelper.close();
    }

    public Integer GetRunwaysCount()
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) c FROM " + DBHelper.RUNWAY_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("c"));
        }
        return count;
    }

    public RunwaysList loadRunwaysByAirport(Airport airport)
    {
        RunwaysList runways = null;

        String query = "SELECT * FROM " + DBHelper.RUNWAY_TABLE_NAME +
                " WHERE " + DBHelper.C_airport_ref + "=" + airport.id;


        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount()>0)
        {
            runways = new RunwaysList();
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Runway runway = cursorToRunway(cursor);
                runways.add(runway);
                cursor.moveToNext();
            }

        }

        return runways;
    }

    private Runway cursorToRunway(Cursor cursor)
    {
        Runway runway = new Runway();
        runway.airport_ref = cursor.getInt(cursor.getColumnIndex(DBHelper.C_airport_ref));
        runway.airport_ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_airport_ident));
        runway.length_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_length));
        runway.width_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_width));
        runway.surface = cursor.getString(cursor.getColumnIndex(DBHelper.C_surface));
        runway.id = cursor.getInt(cursor.getColumnIndex("id"));
        runway.le_ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_le_ident));
        runway.le_latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_le_latitude));
        runway.le_longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_le_longitude));
        runway.le_elevation_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_le_elevation));
        runway.le_heading_degT = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_le_heading));
        runway.le_displaced_threshold_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_le_displaced_threshold));
        runway.he_ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_he_ident));
        runway.he_latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_he_latitude));
        runway.he_longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_he_longitude));
        runway.he_elevation_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_he_elevation));
        runway.he_heading_degT = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_he_heading));
        runway.he_displaced_threshold_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_he_displaced_threshold));

        return runway;
    }

    private ContentValues getRunwayValues(Runway runway)
    {
        ContentValues values = new ContentValues();
        values.put(DBHelper.C_airport_ref, runway.airport_ref);
        values.put(DBHelper.C_airport_ident, runway.airport_ident);
        values.put(DBHelper.C_length, runway.length_ft);
        values.put(DBHelper.C_width, runway.width_ft);
        values.put(DBHelper.C_surface, runway.surface);
        values.put(DBHelper.C_le_ident, runway.le_ident);
        values.put(DBHelper.C_le_latitude, runway.le_latitude_deg);
        values.put(DBHelper.C_le_longitude, runway.le_longitude_deg);
        values.put(DBHelper.C_le_elevation, runway.le_elevation_ft);
        values.put(DBHelper.C_le_heading, runway.le_heading_degT);
        values.put(DBHelper.C_le_displaced_threshold, runway.le_displaced_threshold_ft);
        values.put(DBHelper.C_he_ident, runway.he_ident);
        values.put(DBHelper.C_he_latitude, runway.he_latitude_deg);
        values.put(DBHelper.C_he_longitude, runway.he_longitude_deg);
        values.put(DBHelper.C_he_elevation, runway.he_elevation_ft);
        values.put(DBHelper.C_he_heading, runway.he_heading_degT);
        values.put(DBHelper.C_he_displaced_threshold, runway.he_displaced_threshold_ft);

        return values;
    }
}

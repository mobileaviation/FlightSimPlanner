package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import nl.robenanita.googlemapstest.Weather.Metar;
import nl.robenanita.googlemapstest.Weather.Notam;
import nl.robenanita.googlemapstest.Weather.Taf;

/**
 * Created by Rob Verhoef on 10-6-2015.
 */
public class AirportInfoDataSource {
    private SQLiteDatabase database;
    private UserDBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public AirportInfoDataSource(Context context) {
        dbHelper = new UserDBHelper(context);
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

    public Cursor GetNotams(String icao)
    {
        String q = "SELECT " + UserDBHelper.C_notam + " FROM " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                + " WHERE "  + UserDBHelper.C_latest + "=1 AND " + UserDBHelper.C_ident + "=\"" + icao + "\";";
        return database.rawQuery(q, null);
    }

    public void ResetNotams(Notam notam)
    {
        String updateLatestQuery = "UPDATE " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                + " SET " + UserDBHelper.C_latest + "=0"
                + " WHERE " + UserDBHelper.C_ident + "=\"" + notam.getStation_id() + "\""
                + " AND " + UserDBHelper.C_latest + "=1"
                + " AND " + UserDBHelper.C_notam_number + "!=''";;
        database.execSQL(updateLatestQuery);
    }

    public void InsertNotam(Notam notam)
    {
        // get Airport...
        // SELECT COUNT(*) c FROM tbl_AirportInfo WHERE notam_number = notam.NotamNumber
        // INSERT INTO tbl_AirportInfo (airport_id, ident, notam, notam_number, notam_date, notam_position, notam_polygon)
        // VALUES(....)
        // UPDATE
        String checkQuery = "SELECT " + UserDBHelper.C_ident + " FROM " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                + " WHERE " + UserDBHelper.C_notam_number + "=\"" + notam.NotamNumber + "\";";
        Cursor cursor = database.rawQuery(checkQuery, null);

        if (cursor.getCount()>0)
        {
            Log.i(TAG, "found notam: " + notam.getStation_id() + " So update.." );
            String updateQuery = "UPDATE " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                    + " SET " + UserDBHelper.C_notam + "=\"" + notam.raw_text.replace("\"", "'") + "\","
                    + " " + UserDBHelper.C_latest + "=1"
                    + " WHERE " + UserDBHelper.C_notam_number + "='" + notam.NotamNumber + "';";
            database.execSQL(updateQuery);
        }
        else
        {
            Log.i(TAG, "Not found notam: " + notam.getStation_id() + " So insert.." );
            String insertQuery = "INSERT INTO " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                    + "(" + UserDBHelper.C_notam + ","
                    + UserDBHelper.C_notam_date + ","
                    + UserDBHelper.C_notam_number + ","
                    + UserDBHelper.C_ident + ","
                    + UserDBHelper.C_airport_id + ","
                    + UserDBHelper.C_notam_polygon + ","
                    + UserDBHelper.C_notam_position + ","
                    + UserDBHelper.C_latest
                    + ") VALUES("
                    + "\"" + notam.raw_text.replace("\"", "'") + "\","
                    + notam.GetStartDate().getTime() + ","
                    + "\"" + notam.NotamNumber + "\","
                    + "\"" + notam.getStation_id() + "\","
                    + ((notam.airport!=null)? notam.airport.id : -1) + ","
                    + "\"\","
                    + "\"" + notam.getLocationString() + "\","
                    + "1"
                    + ")";
            database.execSQL(insertQuery);
        }
    }

    public void ResetMetars(Metar metar)
    {
        String updateLatestQuery = "UPDATE " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                + " SET " + UserDBHelper.C_latest + "=0"
                + " WHERE " + UserDBHelper.C_ident + "=\"" + metar.station_id + "\""
                + " AND " + UserDBHelper.C_latest + "=1"
                + " AND " + UserDBHelper.C_metar + "!=''";
        database.execSQL(updateLatestQuery);
    }

    public void InsertMetar(Metar metar)
    {
        String checkQuery = "SELECT " + UserDBHelper.C_ident + " FROM " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                + " WHERE " + UserDBHelper.C_metar_date + "=" + metar.GetObservationTime().getTime() + " AND "
                + UserDBHelper.C_ident + "=\"" + metar.station_id + "\";";
        Cursor cursor = database.rawQuery(checkQuery, null);

        if (cursor.getCount()>0)
        {
            Log.i(TAG, "found metar: " + metar.station_id + " So update.." );
            String updateQuery = "UPDATE " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                    + " SET " + UserDBHelper.C_metar + "=\"" + metar.raw_text.replace("\"", "'") + "\","
                    + " " + UserDBHelper.C_latest + "=1"
                    + " WHERE " + UserDBHelper.C_metar_date + "=" + metar.GetObservationTime().getTime() + " AND "
                    + UserDBHelper.C_ident + "=\"" + metar.station_id + "\";";
            database.execSQL(updateQuery);
        }
        else
        {
            Log.i(TAG, "Not found metar: " + metar.station_id + " So insert.." );
            String insertQuery = "INSERT INTO " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                    + "(" + UserDBHelper.C_metar + ","
                    + UserDBHelper.C_metar_date + ","
                    + UserDBHelper.C_ident + ","
                    + UserDBHelper.C_airport_id + ","
                    + UserDBHelper.C_notam_position + ","
                    + UserDBHelper.C_latest
                    + ") VALUES("
                    + "\"" + metar.raw_text.replace("\"", "'") + "\","
                    + metar.GetObservationTime().getTime() + ","
                    + "\"" + metar.station_id + "\","
                    + metar.airport.id + ","
                    + "\"" + metar.airport.getPointString() + "\","
                    + "1"
                    + ")";
            database.execSQL(insertQuery);
        }
    }

    public void ResetTafs(Taf taf)
    {
        String updateLatestQuery = "UPDATE " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                + " SET " + UserDBHelper.C_latest + "=0"
                + " WHERE " + UserDBHelper.C_ident + "=\"" + taf.station_id + "\""
                + " AND " + UserDBHelper.C_latest + "=1"
                + " AND " + UserDBHelper.C_taf +"!=''";
        database.execSQL(updateLatestQuery);
    }

    public void InsertTaf(Taf taf)
    {
        String checkQuery = "SELECT " + UserDBHelper.C_ident + " FROM " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                + " WHERE " + UserDBHelper.C_taf_date + "=" + taf.GetIssueTime().getTime() + " AND "
                + UserDBHelper.C_ident + "=\"" + taf.station_id + "\";";
        Cursor cursor = database.rawQuery(checkQuery, null);
        if (cursor.getCount()>0)
        {
            Log.i(TAG, "found TAF: " + taf.station_id + " So update.." );
            String updateQuery = "UPDATE " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                    + " SET " + UserDBHelper.C_taf + "=\"" + taf.raw_text.replace("\"", "'") + "\","
                    + " " + UserDBHelper.C_latest + "=1"
                    + " WHERE " + UserDBHelper.C_taf_date + "=" + taf.GetIssueTime().getTime() + " AND "
                    + UserDBHelper.C_ident + "=\"" + taf.station_id + "\";";
            database.execSQL(updateQuery);
        }
        else
        {
            Log.i(TAG, "Not found TAF: " + taf.station_id + " So insert.." );
            String insertQuery = "INSERT INTO " + UserDBHelper.AIRPORTINFO_TABLE_NAME
                    + "(" + UserDBHelper.C_taf + ","
                    + UserDBHelper.C_taf_date + ","
                    + UserDBHelper.C_ident + ","
                    + UserDBHelper.C_airport_id + ","
                    + UserDBHelper.C_notam_position + ","
                    + UserDBHelper.C_latest
                    + ") VALUES("
                    + "\"" + taf.raw_text.replace("\"", "'") + "\","
                    + taf.GetIssueTime().getTime() + ","
                    + "\"" + taf.station_id + "\","
                    + taf.airport.id + ","
                    + "\"" + taf.airport.getPointString() + "\","
                    + "1"
                    + ")";
            database.execSQL(insertQuery);
        }
    }



}

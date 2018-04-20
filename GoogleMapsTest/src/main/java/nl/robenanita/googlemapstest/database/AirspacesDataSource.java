package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vividsolutions.jts.geom.Coordinate;

import java.io.File;

import nl.robenanita.googlemapstest.Settings.LayersSetup.LayersOfflineSetupFragment;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class AirspacesDataSource {
    public AirspacesDataSource(Context context)
    {
        this.context = context;
    }

    private Context context;

    private String TAG = "GoogleMapsTest";
    private SQLiteDatabase database;
    private Integer retryCopyCount;

    public Boolean Open(String database)
    {
        String databasename = DBFilesHelper.DatabasePath(context) + database;

        File f = new File(databasename);
        if (!f.exists())
        {
            DBFilesHelper.CopyDatabases(context, true);
        }

        try {
            this.database = SQLiteDatabase.openDatabase(databasename, null, SQLiteDatabase.OPEN_READONLY);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error opening Airspaces database: " + e.getMessage() + ". try copy from assets");
            try {
                Log.e(TAG, "Try to delete and recopy from assets: " + databasename);
                context.deleteDatabase(database);
                DBFilesHelper.CopyDatabases(context, true);
                this.database = SQLiteDatabase.openDatabase(databasename, null, SQLiteDatabase.OPEN_READWRITE);
                return true;
            }
            catch (Exception ee)
            {
                Log.e(TAG, "Tryed to delete and recopy from assets but exception: " + ee.getMessage());
            }
            return false;
        }
    }

    public void Close()
    {
        this.database.close();
    }

    public Cursor GetAirspaces(String country)
    {
        String[] selectionarg = new String[1];
        selectionarg[0] = country;
        String sql = "SELECT * FROM tbl_airspaces WHERE country=?;";// AND _id=6;";// (_id=7 OR _id=9 OR _id=6);";
        return this.database.rawQuery(sql, selectionarg);
    }

    public Cursor GetAirspacesByCoordinate(Coordinate coordinate)
    {
        String sql = "SELECT * FROM tbl_Airspaces " +
                " WHERE #LAT#<lat_top_left " +
                "AND #LAT#>lat_bottom_right " +
                "AND #LON#>lon_top_left " +
                "AND #LON#<lot_bottom_right;";
        sql = sql.replace("#LON#", Double.toString(coordinate.x));
        sql = sql.replace("#LAT#", Double.toString(coordinate.y));
        Log.i("AirspacesDataSource", sql);
        return this.database.rawQuery(sql, null);
    }

}

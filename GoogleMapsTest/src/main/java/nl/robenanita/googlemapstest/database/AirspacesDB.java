package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vividsolutions.jts.geom.Coordinate;

import java.io.File;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class AirspacesDB {
    public AirspacesDB(Context context)
    {
        this.context = context;
    }

    private Context context;

    private SQLiteDatabase database;

    public Boolean Open(String database)
    {
        String databasename = DBFilesHelper.DatabasePath(context) + database;

        File f = new File(databasename);
        if (!f.exists())
        {
            DBFilesHelper.CopyDatabases(context, true);
        }

        try {
            this.database = SQLiteDatabase.openDatabase(databasename, null, SQLiteDatabase.OPEN_READWRITE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
        Log.i("AirspacesDB", sql);
        return this.database.rawQuery(sql, null);
    }

}

package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.robenanita.googlemapstest.Fix;

/**
 * Created by Rob Verhoef on 6-4-14.
 */
public class FixesDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Integer pid;

    public FixesDataSource(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void open(Integer pid){
        this.pid = pid;
        try {
            dbHelper.createDataBase();
            database = dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        // Removed due to IlligalOperations Exceptions
        //dbHelper.close();
    }

    public Integer GetFixesCount()
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) c FROM " + DBHelper.FIXES_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("c"));
        }
        return count;
    }

    public Fix GetFixByID(Integer id)
    {
        String query = "SELECT * FROM " + DBHelper.FIXES_TABLE_NAME +
            " WHERE _id=" + Integer.toString(id) + ";";

        Fix fix = null;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            fix = cursorToFix(cursor);
        }

        return fix;
    }

    public Map<Integer, Fix> SearchFix(String searchTerm)
    {
        HashMap<Integer, Fix> fixes = new HashMap<Integer, Fix>();

        String query = "SELECT * FROM " + DBHelper.FIXES_TABLE_NAME +
                " WHERE "  + DBHelper.C_name  + " LIKE '%" + searchTerm + "%' OR "
                + DBHelper.C_ident + " LIKE '%" + searchTerm + "%' "
                + " ORDER BY " + DBHelper.C_name + " LIMIT 100;";
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Fix fix = cursorToFix(cursor);
            fixes.put(fix.id, fix);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return fixes;
    }

    public Map<Integer, Fix> getFixesByCoordinateAndZoomLevel(LatLngBounds boundary, Float zoomLevel, Map<Integer, Fix> curFixes)
    {
        Map<Integer, Fix> fixes;
        if (curFixes==null) fixes = new HashMap<Integer, Fix>();
        else
            fixes = curFixes;

        if (zoomLevel>7) {

            String where = "WHERE ";

            String latBetween = "";
            latBetween = "latitude_deg BETWEEN " + Double.toString(boundary.southwest.latitude)
                    + " AND " + Double.toString(boundary.northeast.latitude);

            String lonBetween = "";
            lonBetween = "longitude_deg BETWEEN " + Double.toString(boundary.southwest.longitude)
                    + " AND " + Double.toString(boundary.northeast.longitude);

            where = where + latBetween + " AND " + lonBetween;

            String query = "SELECT A.* FROM " + DBHelper.FIXES_TABLE_NAME + " A "  + where;

            Log.i(TAG, "Fixes Query: " + query);

            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Fix fix = cursorToFix(cursor);
                if (!fixes.containsKey(fix.id))
                    fixes.put(fix.id, fix);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }

        return fixes;
    }

    private Fix cursorToFix(Cursor cursor) {
        Fix fix = new Fix();
        fix.ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_ident));
        fix.id = cursor.getInt(cursor.getColumnIndex("_id"));
        fix.latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_latitude_deg));
        fix.longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_longitude_deg));
        fix.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));

        return fix;
    }
}

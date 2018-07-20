package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.robenanita.googlemapstest.Navaid;

/**
 * Created by Rob Verhoef on 6-4-14.
 */
public class NavaidsDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Integer pid;

    public NavaidsDataSource(Context context) {
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

    public Integer GetNavaidsCount()
    {
        Integer count = 0;
        String query = "SELECT COUNT(*) c FROM " + DBHelper.NAVAIDS_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("c"));
        }
        return count;
    }

    public Navaid GetNavaidByID(Integer id)
    {
        String query = "SELECT * FROM " + DBHelper.NAVAIDS_TABLE_NAME +
                " WHERE " + DBHelper.C_id + "=" + Integer.toString(id) + ";";

        Navaid navaid = null;

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            navaid = cursorToNavaid(cursor);
        }

        return navaid;
    }

    public ArrayList<Navaid> GetNaviadsByBoundary(LatLngBounds boundary)
    {
        ArrayList<Navaid> navaids = new ArrayList<>();

            String where = "WHERE ";

            String latBetween = "";
            latBetween = "latitude_deg BETWEEN " + Double.toString(boundary.southwest.latitude)
                    + " AND " + Double.toString(boundary.northeast.latitude);

            String lonBetween = "";
            lonBetween = "longitude_deg BETWEEN " + Double.toString(boundary.southwest.longitude)
                    + " AND " + Double.toString(boundary.northeast.longitude);

            where = where + " " + latBetween + " AND " + lonBetween;

            String query = "SELECT * FROM tbl_Navaids " + where;

            Log.i(TAG, "Navaids Query: " + query);

            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                Navaid navaid = cursorToNavaid(cursor);
                navaids.add(navaid);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();

        return navaids;
    }

    public Map<Integer, Navaid> GetNaviadsByCoordinateAndZoomLevel(LatLngBounds boundary, Float zoomLevel, Map<Integer, Navaid> curNavaids)
    {
        Map<Integer, Navaid> navaids;
        if (curNavaids == null) navaids = new HashMap<Integer, Navaid>();
        else navaids = curNavaids;

        if (zoomLevel>7.9f)
        {
            String where = "WHERE ";

            String latBetween = "";
            latBetween = "latitude_deg BETWEEN " + Double.toString(boundary.southwest.latitude)
                    + " AND " + Double.toString(boundary.northeast.latitude);

            String lonBetween = "";
            lonBetween = "longitude_deg BETWEEN " + Double.toString(boundary.southwest.longitude)
                    + " AND " + Double.toString(boundary.northeast.longitude);

            where = where + " " + latBetween + " AND " + lonBetween;

            String query = "SELECT * FROM tbl_Navaids " + where;

            Log.i(TAG, "Navaids Query: " + query);

            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                Navaid navaid = cursorToNavaid(cursor);
                if (!navaids.containsKey(navaid.id))
                    navaids.put(navaid.id, navaid);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }

        return navaids;
    }

    public Map<Integer, Navaid> SearchNavaidsByNameOrCode(String searchTerm)
    {
        Map<Integer, Navaid> navaids = new HashMap<Integer, Navaid>();

        String query = "SELECT * FROM " + DBHelper.NAVAIDS_TABLE_NAME +
                " WHERE " + DBHelper.C_name  + " LIKE '%" + searchTerm + "%' OR "
                + DBHelper.C_ident + " LIKE '%" + searchTerm + "%' "
                + " ORDER BY " + DBHelper.C_name + " LIMIT 100;";
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Navaid navaid = cursorToNavaid(cursor);
            navaids.put(navaid.id, navaid);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return navaids;
    }

    private Navaid cursorToNavaid(Cursor cursor) {

        Navaid navaid = new Navaid();
        navaid.ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_ident));
        navaid.associated_airport = cursor.getString(cursor.getColumnIndex(DBHelper.C_associated_airport));
        //navaid.associated_airport_id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_associated_airport_id));
        navaid.latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_latitude_deg));
        navaid.longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_longitude_deg));
        navaid.frequency_khz = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_frequency));
        navaid.filename = cursor.getString(cursor.getColumnIndex(DBHelper.C_filename));
        navaid.power = cursor.getString(cursor.getColumnIndex(DBHelper.C_power));
        navaid.id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_id));
        navaid.iso_country = cursor.getString(cursor.getColumnIndex(DBHelper.C_iso_country));
        navaid.dme_channel = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_dme_channel));
        navaid.dme_elevation_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_dme_elevation_ft));
        navaid.dme_frequency_khz = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_dme_frequency_khz));
        navaid.dme_latitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_dme_latitude_deg));
        navaid.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        navaid.dme_longitude_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_dme_longitude_deg));
        navaid.type = Navaid.ParseNavaidType(cursor.getString(cursor.getColumnIndex(DBHelper.C_type)));
        navaid.magnetic_variation_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_magnetic_variation_deg));
        navaid.slaved_variation_deg = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_slaved_variation_deg));
        navaid.elevation_ft = cursor.getInt(cursor.getColumnIndex(DBHelper.C_elevation_ft));
        navaid.usageType = cursor.getString(cursor.getColumnIndex(DBHelper.C_usageType));

        //navaid.slaved_elevation_ft = (cursor.isNull(cursor.getColumnIndex(DBHelper.C_Version))) ? 0 : cursor.getInt(cursor.getColumnIndex(DBHelper.C_Version));
        //navaid.usageType = (cursor.isNull(cursor.getColumnIndex(DBHelper.C_heading))) ? 0 : cursor.getDouble(cursor.getColumnIndex(DBHelper.C_heading));

        return navaid;
    }
}

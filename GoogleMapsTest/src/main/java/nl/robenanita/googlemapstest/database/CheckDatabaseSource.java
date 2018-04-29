package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

import nl.robenanita.googlemapstest.Property;

/**
 * Created by Rob Verhoef on 25-5-2014.
 */
public class CheckDatabaseSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";
    private Context c;

    public Property DBVersion;

    public CheckDatabaseSource(Context context) {
        c = context;
        dbHelper = DBHelper.getInstance(context);
    }

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

    public void checkVersion()
    {
        //dropUnusedTables();

        DBVersion = getProperty("DB_VERSION");
        this.close();
        if (DBVersion != null)
        {
            Integer v = Integer.parseInt(DBVersion.value1);
            dbHelper.deleteDatabase(v);
        }
    }

    private void dropUnusedTables()
    {
        database.execSQL("DROP TABLE IF EXISTS " + UserDBHelper.FLIGHTPLAN_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + UserDBHelper.USERWAYPOINT_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + UserDBHelper.TRACKPOINTS_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + UserDBHelper.TRACKS_TABLE_NAME);
    }

    private Property getProperty(String name)
    {
        String query = "SELECT * FROM " + DBHelper.PROPERTIES_TABLE_NAME
                + " WHERE name='" + name + "';";

        Cursor cursor = database.rawQuery(query, null);

        Property p = null;
        if (cursor.moveToFirst())
        {
            p = cursorToProperty(cursor);
        }

        Log.i(TAG, "Property: " + p.name + " with value1: " + p.value1 + " and value2: " + p.value2);
        return p;
    }

    private Property cursorToProperty(Cursor cursor)
    {
        Property property = new Property();
        property._id = cursor.getInt(cursor.getColumnIndex("_id"));
        property.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        property.value1 = cursor.getString(cursor.getColumnIndex("value1"));
        property.value2 = cursor.getString(cursor.getColumnIndex("value2"));

        return property;
    }
}

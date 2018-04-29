package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Continent;

/**
 * Created by Rob Verhoef on 25-2-14.
 */
public class ContinentDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public Boolean Updated = false;

    private String[] allColumns = {
            DBHelper.C_id,
            DBHelper.C_code,
            DBHelper.C_name,
    };

    public ContinentDataSource(Context context)
    {
        dbHelper = DBHelper.getInstance(context);
    }

    public void open() {
        try
        {
            dbHelper.createDataBase();
            database = dbHelper.openDataBase();
            Updated = dbHelper.updated;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<Continent> getAllContinents(boolean emptyStart) {
        ArrayList<Continent> continents = new ArrayList<Continent>();
        if (emptyStart)
        {
            Continent empty = new Continent();
            empty.code = "";
            empty.name = "Nothing selected";
            empty.id = -1;
            continents.add(empty);
        }

        Cursor cursor = database.query(DBHelper.CONTINENT_TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Continent continent = cursorToCountry(cursor);
            continents.add(continent);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return continents;
    }

    public boolean insertContinents(ArrayList<Continent> continents)
    {
        database.beginTransaction();
        try
        {
            for (Continent continent : continents)
            {
                ContentValues values = createContentValues(continent);
                String[] args = {continent.code};
                database.delete(DBHelper.CONTINENT_TABLE_NAME, DBHelper.C_code+ " =?", args);
                database.insert(DBHelper.CONTINENT_TABLE_NAME, null, values);

            }
            database.setTransactionSuccessful();
        }
        catch (Exception ee)
        {
            database.endTransaction();
            Log.i(TAG, "Database error: " + ee.getMessage());
            return false;
        }

        database.endTransaction();
        return true;
    }

    private ContentValues createContentValues(Continent continent)
    {
        ContentValues values = new ContentValues();
        values.put(DBHelper.C_id, continent.id);
        values.put(DBHelper.C_name, continent.name);
        values.put(DBHelper.C_code, continent.code);

        return values;
    }

    private Continent cursorToCountry(Cursor cursor) {
        Continent continent = new Continent();
        continent.code = cursor.getString(cursor.getColumnIndex(DBHelper.C_code));
        continent.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        continent.id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_id));

        return continent;
    }
}

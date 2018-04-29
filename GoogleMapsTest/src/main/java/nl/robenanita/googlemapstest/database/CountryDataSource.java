package nl.robenanita.googlemapstest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Country;

/**
 * Created by Rob Verhoef on 9-2-14.
 */
public class CountryDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public Boolean Updated = false;

    private String[] allColumns = {
            DBHelper.C_id,
            DBHelper.C_code,
            DBHelper.C_name,
            DBHelper.C_continent,
            DBHelper.C_wikipedia_link,
            DBHelper.C_keywords
    };

    public CountryDataSource(Context context) {
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

    public ArrayList<Country> getAllCountries(boolean emptyStart) {
        ArrayList<Country> countries = new ArrayList<Country>();

        if (emptyStart)
        {
            Country empty = new Country();
            empty.id = -1;
            empty.name = "Nothing Selected";
            empty.code = "";
            countries.add(empty);
        }

        Cursor cursor = database.query(DBHelper.COUNTRY_TABLE_NAME,
                allColumns, null, null, null, null, DBHelper.C_name);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Country country = cursorToCountry(cursor);
            countries.add(country);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return countries;
    }

    public boolean insertCountries(ArrayList<Country> countries)
    {
        database.beginTransaction();
        try
        {
            for (Country country : countries)
            {
                ContentValues values = createContentValues(country);
                String[] args = {country.code};
                database.delete(DBHelper.COUNTRY_TABLE_NAME, DBHelper.C_code+ " =?", args);
                database.insert(DBHelper.COUNTRY_TABLE_NAME, null, values);

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

    private ContentValues createContentValues(Country country)
    {
        ContentValues values = new ContentValues();
        values.put(DBHelper.C_id, country.id);
        values.put(DBHelper.C_name, country.name);
        values.put(DBHelper.C_code, country.code);
        values.put(DBHelper.C_continent, country.continent);
        values.put(DBHelper.C_wikipedia_link, country.wikipedia_link);
        values.put(DBHelper.C_keywords, country.keywords);

        return values;
    }

    private Country cursorToCountry(Cursor cursor) {
        Country country = new Country();
        country.continent = cursor.getString(cursor.getColumnIndex(DBHelper.C_continent));
        country.code = cursor.getString(cursor.getColumnIndex(DBHelper.C_code));
        country.wikipedia_link = cursor.getString(cursor.getColumnIndex(DBHelper.C_wikipedia_link));
        country.name = cursor.getString(cursor.getColumnIndex(DBHelper.C_name));
        country.keywords = cursor.getString(cursor.getColumnIndex(DBHelper.C_keywords));
        country.id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_id));

        return country;
    }
}

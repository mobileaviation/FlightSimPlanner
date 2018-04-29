package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Airport.Frequency;

/**
 * Created by Rob Verhoef on 22-4-14.
 */
public class FrequenciesDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public FrequenciesDataSource(Context context) {
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

    public ArrayList<Frequency> loadFrequenciesByAirport(Airport airport)
    {
        ArrayList<Frequency> frequencies = null;

        String query = "SELECT * FROM " + DBHelper.FREQUENCIES_TABLE_NAME +
                " WHERE " + DBHelper.C_airport_ref + "=" + airport.id;

        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount()>0)
        {
            frequencies = new ArrayList<Frequency>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Frequency frequency = cursorToFrequency(cursor);
                frequencies.add(frequency);
                cursor.moveToNext();
            }

        }

        return frequencies;
    }

    private Frequency cursorToFrequency(Cursor cursor)
    {
        Frequency frequency = new Frequency();
        frequency._id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_airport_ref));
        frequency.id = cursor.getInt(cursor.getColumnIndex("_id"));
        frequency.airport_ref = cursor.getInt(cursor.getColumnIndex(DBHelper.C_airport_ref));
        frequency.airport_ident = cursor.getString(cursor.getColumnIndex(DBHelper.C_airport_ident));
        frequency.type = cursor.getString(cursor.getColumnIndex(DBHelper.C_type));
        frequency.description = cursor.getString(cursor.getColumnIndex(DBHelper.C_description));
        frequency.frequency_mhz = cursor.getDouble(cursor.getColumnIndex(DBHelper.C_frequency_mhz));


        return frequency;
    }
}

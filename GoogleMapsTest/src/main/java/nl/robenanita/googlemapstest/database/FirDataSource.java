package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Weather.Fir;
import nl.robenanita.googlemapstest.Weather.Station;

/**
 * Created by Rob Verhoef on 23-6-2015.
 */
public class FirDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String TAG = "GooglemapsTest";

    public FirDataSource(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void open() {
        try
        {
            dbHelper.createDataBase();
            database = dbHelper.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        //dbHelper.close();
    }

    public ArrayList<Fir> GetPossibleFirs(ArrayList<Station> stations)
    {
        ArrayList<Fir> firs = new ArrayList<Fir>();

        ArrayList<String> st = new ArrayList<String>();
        String where = "";
        Boolean firsFound = false;
        for (Station station : stations)
        {
            String s = station.station_id.substring(0, 2);
            if (!st.contains(s)) {
                st.add(s);
                where = where + "ident like \"" + s + "%\" OR ";
                firsFound = true;
            }
        }

        if (firsFound) {
            where = where.substring(0, where.length() - 4) + ";";
            String query = "SELECT id, ident FROM tbl_Firs WHERE " + where + ";";


            Cursor c = database.rawQuery(query, null);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Fir fir = new Fir();
                fir.id = c.getInt(c.getColumnIndex("id"));
                fir.ident = c.getString(c.getColumnIndex("ident"));
                firs.add(fir);

                c.moveToNext();
            }
        }

        return (firs.size()>0) ? firs : null;
    }
}

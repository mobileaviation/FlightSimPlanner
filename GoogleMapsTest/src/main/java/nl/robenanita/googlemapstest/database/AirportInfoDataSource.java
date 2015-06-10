package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

}

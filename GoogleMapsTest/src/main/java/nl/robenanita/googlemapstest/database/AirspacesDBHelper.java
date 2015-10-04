package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Rob Verhoef on 4-10-2015.
 */
public class AirspacesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "airspaces.db";
    private static final int DATABASE_VERSION = 1;

    public static final String AIRSPACES_TABLE_NAME = "tbl_Airspaces";

    private static final String TAG = "GooglemapsTest";

    public static final String C_name = "name";
    public static final String C_version = "version";
    public static final String C_category = "category";
    public static final String C_id = "id";
    public static final String C_country = "country";
    public static final String C_altLimit_top = "altLimit_top";
    public static final String C_altLimit_top_unit = "altLimit_top_unit";
    public static final String C_altLimit_top_ref = "altLimit_top_ref";
    public static final String C_altLimit_bottom = "altLimit_bottom";
    public static final String C_altLimit_bottom_unit = "altLimit_bottom_unit";
    public static final String C_altLimit_bottom_ref = "altLimit_bottom_ref";
    public static final String C_geometry = "geometry";
    public static final String C_lat_top_left = "lat_top_left";
    public static final String C_lon_top_left = "lon_top_left";
    public static final String C_lat_bottom_right = "lat_bottom_right";
    public static final String C_lot_bottom_right = "lot_bottom_right";

    public static final String AIRSPACES_TABLE = "create table " +
            AIRSPACES_TABLE_NAME + " (_id integer primary key autoincrement, " +
            C_name + " text, " +
            C_version + " text, " +
            C_category + " text, " +
            C_id + " text, " +
            C_country + " text, " +
            C_altLimit_top + " integer, " +
            C_altLimit_top_unit + " text, " +
            C_altLimit_top_ref + " text, " +
            C_altLimit_bottom + " integer, " +
            C_altLimit_bottom_unit + " text, " +
            C_altLimit_bottom_ref + " text, " +
            C_geometry + " text, " +
            C_lat_top_left + " real, " +
            C_lon_top_left + " real, " +
            C_lat_bottom_right + " real, " +
            C_lot_bottom_right + " real );";

    public static final String AIRSPACES_ENVELOPE_INDEX = "create index envelope_index " +
            "on " + AIRSPACES_TABLE_NAME + " (" +
            C_lat_top_left + "," + C_lon_top_left + "," + C_lat_bottom_right + "," + C_lot_bottom_right +
            ");";


    private void createTables(SQLiteDatabase database)
    {
        database.execSQL(AIRSPACES_TABLE);
        Log.i(TAG, "Airspaces Table Created..");
        database.execSQL(AIRSPACES_ENVELOPE_INDEX);
        Log.i(TAG, "Airspaces Envelope index Created");
    }


    public AirspacesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

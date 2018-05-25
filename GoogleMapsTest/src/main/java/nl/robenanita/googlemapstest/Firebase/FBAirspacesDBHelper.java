package nl.robenanita.googlemapstest.Firebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FBAirspacesDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fbairspaces.db";
    private static final int DATABASE_VERSION = 1;

    public static final String AIRSPACES_TABLE_NAME = "tbl_Airspaces";

    private static final String TAG = "GooglemapsTest";

    public static final String C_name = "name";
    public static final String C_version = "version";
    public static final String C_category = "category";
    public static final String C_airspace_id = "airspace_id";
    public static final String C_country = "country";
    public static final String C_altLimit_top = "altLimit_top";
    public static final String C_altLimit_top_unit = "altLimit_top_unit";
    public static final String C_altLimit_top_ref = "altLimit_top_ref";
    public static final String C_altLimit_bottom = "altLimit_bottom";
    public static final String C_altLimit_bottom_unit = "altLimit_bottom_unit";
    public static final String C_altLimit_bottom_ref = "altLimit_bottom_ref";
    public static final String C_geometry = "geometry";
    public static final String C_envelope = "envelope";
    public static final String C_lat_top_left = "lat_top_left";
    public static final String C_lon_top_left = "lon_top_left";
    public static final String C_lat_bottom_right = "lat_bottom_right";
    public static final String C_lon_bottom_right = "lot_bottom_right";

    public static final String AIRSPACES_TABLE = "create table " +
            AIRSPACES_TABLE_NAME + " (_id integer primary key autoincrement, " +
            C_name + " text, " +
            C_version + " text, " +
            C_category + " text, " +
            C_airspace_id + " text, " +
            C_country + " text, " +
            C_altLimit_top + " integer, " +
            C_altLimit_top_unit + " text, " +
            C_altLimit_top_ref + " text, " +
            C_altLimit_bottom + " integer, " +
            C_altLimit_bottom_unit + " text, " +
            C_altLimit_bottom_ref + " text, " +
            C_geometry + " blob, " +
            C_lat_top_left + " double, " +
            C_lon_top_left + " double, " +
            C_lat_bottom_right + " double, " +
            C_lon_bottom_right + " double, " +
            C_envelope + " blob );";

    public static final String AIRSPACES_COUNTRY_INDEX = "create index country_index " +
            "on " + AIRSPACES_TABLE_NAME + " (" +
            C_country +
            ");";

    public static final String AIRSPACES_ENVELOPE_INDEX = "create index envelope_index " +
            "on " + AIRSPACES_TABLE_NAME + " (" +
            C_lat_top_left + "," + C_lon_top_left + "," + C_lat_bottom_right + "," + C_lon_bottom_right +
            ");";

    private Context context;
    public static SQLiteDatabase database;
    private static FBAirspacesDBHelper sInstance;

    public static synchronized FBAirspacesDBHelper getInstance(Context context)
    {
        if (sInstance == null) {
            sInstance = new FBAirspacesDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private FBAirspacesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public SQLiteDatabase Open()
    {
        database = getWritableDatabase();
        return database;
    }

    @Override
    public void close()
    {
        database.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AIRSPACES_TABLE);
        sqLiteDatabase.execSQL(AIRSPACES_COUNTRY_INDEX);
        sqLiteDatabase.execSQL(AIRSPACES_ENVELOPE_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

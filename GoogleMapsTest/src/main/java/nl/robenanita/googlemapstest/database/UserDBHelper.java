package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Rob Verhoef on 18-1-14.
 */
public class UserDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userairnav.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TRACKS_TABLE_NAME = "tbl_Tracks";
    public static final String TRACKPOINTS_TABLE_NAME = "tbl_Trackpoints";
    public static final String PROPERTIES_TABLE_NAME = "tbl_Properties";
    public static final String FLIGHTPLAN_TABLE_NAME = "tbl_Flightplans";
    public static final String USERWAYPOINT_TABLE_NAME = "tbl_Userwaypoints";

    private static final String TAG = "GooglemapsTest";

    public Boolean updated = false;

    public static final String C_name = "name";
    public static final String C_time = "time";
    public static final String C_date = "date";
    public static final String C_trackid = "track_id";
    public static final String C_latitude_deg = "latitude_deg";
    public static final String C_longitude_deg = "longitude_deg";
    public static final String C_altitude_ft = "altitude_ft";
    public static final String C_ground_speed_kt = "ground_speed_kt";
    public static final String C_indicated_airspeed_kt = "indicated_airspeed_kt";
    public static final String C_value1 = "value1";
    public static final String C_value2 = "value2";
    public static final String C_departure_airport_id = "departure_airport_id";
    public static final String C_destination_airport_id = "destination_airport_id";
    public static final String C_alternate_airport_id = "alternate_airport_id";
    public static final String C_wind_speed_kt = "wind_speed_kt";
    public static final String C_wind_direction_deg = "wind_direction_deg";
    public static final String C_fix_id = "fix_id";
    public static final String C_navaid_id = "navaid_id";
    public static final String C_airport_id = "airport_id";
    public static final String C_flightplan_id = "flightplan_id";
    public static final String C_frequency_khz = "frequency_khz";
    public static final String C_eto = "eto";
    public static final String C_ato = "ato";
    public static final String C_reto = "reto";
    public static final String C_time_leg = "time_leg";
    public static final String C_time_total = "time_total";
    public static final String C_compass_heading_deg = "compass_heading_deg";
    public static final String C_magnetic_heading_deg = "magnetic_heading_deg";
    public static final String C_true_heading_deg = "true_heading_deg";
    public static final String C_true_track_deg = "true_track_deg";
    public static final String C_distance_leg = "distance_leg";
    public static final String C_distance_total = "distance_total";
    public static final String C_sortorder = "sortorder";

    // Database creation sql statement
    private static final String FLIGHTPLAN_TABLE = "create table "
            + FLIGHTPLAN_TABLE_NAME +" (_id integer primary key autoincrement, "
            + C_name + " text, "
            + C_departure_airport_id + " integer, "
            + C_destination_airport_id + " integer, "
            + C_alternate_airport_id + " integer, "
            + C_altitude_ft + " integer, "
            + C_indicated_airspeed_kt + " integer, "
            + C_wind_speed_kt + " integer, "
            + C_wind_direction_deg + " integer, "
            + C_date + " integer"
            + " );";

    private static final String FLIGHTPLAN_TABLE_ADD_DATE = "alter table "
            + FLIGHTPLAN_TABLE_NAME + " add column " + C_date + " integer;";

    private static final String USERWAYPOINT_TABLE = "create table "
            + USERWAYPOINT_TABLE_NAME + " (_id integer primary key autoincrement, "
            + C_name + " text, "
            + C_navaid_id + " integer, "
            + C_fix_id + " integer, "
            + C_latitude_deg + " real, "
            + C_longitude_deg + " real, "
            + C_airport_id + " integer, "
            + C_flightplan_id + " integer, "
            + C_frequency_khz + " real, "
            + C_eto + " integer, "
            + C_ato + " integer, "
            + C_reto + " integer, "
            + C_time_leg + " integer, "
            + C_time_total + " integer, "
            + C_compass_heading_deg + " real, "
            + C_magnetic_heading_deg + " real, "
            + C_true_heading_deg + " real, "
            + C_true_track_deg + " real, "
            + C_distance_leg + " integer, "
            + C_distance_total + " integer, "
            + C_ground_speed_kt + " integer, "
            + C_wind_speed_kt + " integer, "
            + C_wind_direction_deg + " real, "
            + C_altitude_ft + " integer, "
            + C_sortorder + " integer"
            + " );";

    private static final String PROPERTIES_TABLE = "create table "
            + PROPERTIES_TABLE_NAME +" (_id integer primary key autoincrement, "
            + C_name + " text, "
            + C_value1 + " text, "
            + C_value2 + " text"
            + " );";

    private static final String TRACKS_TABLE = "create table "
            + TRACKS_TABLE_NAME +" (_id integer primary key autoincrement, "
            + C_name + " text, "
            + C_time + " integer"
            + " );";

    private static final String TRACKPOINTS_TABLE = "create table "
            + TRACKPOINTS_TABLE_NAME +" (_id integer primary key autoincrement, "
            + C_name + " text, "
            + C_trackid + " integer, "
            + C_longitude_deg + " real, "
            + C_latitude_deg + " real, "
            + C_time + " integer, "
            + C_ground_speed_kt + " real, "
            + C_altitude_ft + " real, "
            + C_true_heading_deg + " real"
            + " );";

    private static final String TRACKPOINTS_LATLON_INDEX = "create index latlon_index" +
            " on tbl_Trackpoints (" +
            " latitude_deg, longitude_deg" +
            ");";

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        insertBasicPropertiesData(db);
    }

    private void createTables(SQLiteDatabase db)
    {
        Log.i(TAG, "Creating Database table Flightplans");
        db.execSQL(FLIGHTPLAN_TABLE);
        Log.i(TAG, "Creating Database table UserWaypoints");
        db.execSQL(USERWAYPOINT_TABLE);
        Log.i(TAG, "Creating Database table TrackPoint");
        db.execSQL(TRACKPOINTS_TABLE);
        Log.i(TAG, "Creating Database table Tracks");
        db.execSQL(TRACKS_TABLE);
        Log.i(TAG, "Creating Database table Properties");
        db.execSQL(PROPERTIES_TABLE);
    }

    private void dropTables(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + FLIGHTPLAN_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USERWAYPOINT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRACKPOINTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRACKS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROPERTIES_TABLE_NAME);
    }

    private void insertBasicPropertiesData(SQLiteDatabase db)
    {
        String i = "INSERT INTO " + PROPERTIES_TABLE_NAME + " (" +
                C_name + ", " + C_value1 + ", " + C_value2 + ") VALUES(";
        db.execSQL(i + "'INIT_POSITION', '52.453917', '5.515624');");
        db.execSQL(i + "'INIT_ZOOM', '8', '');");
        db.execSQL(i + "'INIT_FLIGHTPLAN_ID', '0', '');");
        db.execSQL(i + "'DB_VERSION', '4', '2014-05-15');");
        db.execSQL(i + "'SERVER', '192.168.2.8', '5000');");
        db.execSQL(i + "'INIT_AIRPORT', '2522', '237920,le');");
        db.execSQL(i + "'INSTRUMENTS', 'visible', '1');");
        db.execSQL(i + "'LOCATION', 'Provider', 'sim');");
        db.execSQL(i + "'MARKERS', 'visible', 'test');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Updating Database from version:" + oldVersion + " to version:" + newVersion);
        //dropTables(db);
        //createTables(db);
        //insertBasicPropertiesData(db);
        // add columns to flightplan table
        if (oldVersion<3) db.execSQL(FLIGHTPLAN_TABLE_ADD_DATE);

        if (oldVersion<4) {
            String i = "INSERT INTO " + PROPERTIES_TABLE_NAME + " (" +
                    C_name + ", " + C_value1 + ", " + C_value2 + ") VALUES(";
            db.execSQL(i + "'MARKERS', 'visible', 'test');");
        }

        updated = true;
    }
}
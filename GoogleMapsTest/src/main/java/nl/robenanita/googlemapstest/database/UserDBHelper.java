package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Rob Verhoef on 18-1-14.
 */
public class UserDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "userairnav.db";
    // PRAGMA user_version (= integer)
    private static final int DATABASE_VERSION = 17;

    public static final String TRACKS_TABLE_NAME = "tbl_Tracks";
    public static final String TRACKPOINTS_TABLE_NAME = "tbl_Trackpoints";
    public static final String PROPERTIES_TABLE_NAME = "tbl_Properties";
    public static final String FLIGHTPLAN_TABLE_NAME = "tbl_Flightplans";
    public static final String USERWAYPOINT_TABLE_NAME = "tbl_Userwaypoints";
    public static final String AIRPORTINFO_TABLE_NAME = "tbl_AirportInfo";
    public static final String AIRPORTCHARTS_TABLE_NAME = "tbl_AirportCharts";
    public static final String MBTILES_LOCAL_TABLE_NAME = "tbl_MBTilesLocal";

    private static final String TAG = "GooglemapsTest";

    public Boolean updated = false;

    public static final String C_name = "name";
    public static final String C_time = "time";
    public static final String C_date = "date";
    public static final String C_trackid = "track_id";
    public static final String C_latitude1_deg = "latitude1_deg";
    public static final String C_longitude1_deg = "longitude1_deg";
    public static final String C_latitude2_deg = "latitude2_deg";
    public static final String C_longitude2_deg = "longitude2_deg";
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

    public static final String C_metar = "metar";
    public static final String C_metar_date = "metar_date";
    public static final String C_taf = "taf";
    public static final String C_taf_date = "taf_date";
    public static final String C_notam = "notam";
    public static final String C_notam_date = "notam_date";
    public static final String C_notam_position = "notam_position";
    public static final String C_notam_polygon = "notam_polygon";
    public static final String C_notam_number = "notam_number";
    public static final String C_fir_id = "fir_id";
    public static final String C_ident = "ident";
    public static final String C_latest = "latest";

    public static final String C_url = "url";
    public static final String C_thumbnail_url = "thumbnail_url";
    public static final String C_version = "version";
    public static final String C_created_date = "created_date";
    public static final String C_active = "äctive";
    public static final String C_display_name = "display_name";
    public static final String C_reference_name = "reference_name";
    public static final String C_file_prefix = "file_prefix";
    public static final String C_file_suffix = "file_suffix";

    public static final String C_start_validity = "start_validity";
    public static final String C_end_validity = "end_validity";
    public static final String C_available = "available";
    public static final String C_type = "type";
    public static final String C_visible_order = "visible_order";
    public static final String C_local_file = "local_file";

//    public static void BackupUserDatabase(String databaseName)
//    {
//        OutputStream output = null;
//        FileInputStream fis = null;
//        try {
//            String
//            final String inFileName = DBHelper.DB_PATH + "/" + databaseName;
//            File dbFile = new File(inFileName);
//            fis = new FileInputStream(dbFile);
//
//            String outFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/"+ databaseName + "_copy.db";
//            Log.i(TAG, "Backing up user.db to: " + outFileName);
//
//            // Open the empty db as the output stream
//            output = new FileOutputStream(outFileName);
//
//            // Transfer bytes from the inputfile to the outputfile
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                output.write(buffer, 0, length);
//            }
//            output.flush();
//            output.close();
//            if (fis != null) fis.close();
//
//            Log.i(TAG, "Back up user.db succeeded! ");
//        }
//        catch (Exception ee)
//        {
//            Log.i(TAG, "Backing up user.db error: " + ee.getMessage());
//        }
//    }

    private static final String MBTILES_LOCAL_TABLE = "create table "
            + MBTILES_LOCAL_TABLE_NAME + " (_id integer primary key autoincrement, "
            + C_name + " text,"
            + C_url + " text,"
            + C_version + " integer,"
            + C_type + " text,"
            + C_start_validity + " integer,"
            + C_end_validity + " integer,"
            + C_available + " integer,"
            + C_visible_order + " integer,"
            + C_local_file + " text"
            + " );";

    private static final String MBTILES_LOCAL_TABLE_ADD_VISIBLE = "alter table "
            + MBTILES_LOCAL_TABLE_NAME + " add column " + C_visible_order + " integer;";


    private static final String AIRPORTCHARTS_TABLE = "create table "
            + AIRPORTCHARTS_TABLE_NAME + " (_id integer primary key autoincrement, "
            + C_airport_id + " integer, "
            + C_url + " text, "
            + C_thumbnail_url + " text, "
            + C_version + " text, "
            + C_created_date + " integer, "
            + C_active + " integer, "
            + C_latitude1_deg + " real, "
            + C_longitude1_deg + " real, "
            + C_latitude2_deg + " real, "
            + C_longitude2_deg + " real, "
            + C_display_name + " text, "
            + C_reference_name + " text, "
            + C_file_prefix + " text, "
            + C_file_suffix + " text"
            + " );";

    // Database creation sql statement
    private static final String AIRPORTINFO_TABLE = "create table "
            + AIRPORTINFO_TABLE_NAME +" (_id integer primary key autoincrement, "
            + C_airport_id + " integer, "
            + C_fir_id + " integer, "
            + C_ident + " text, "
            + C_metar + " text, "
            + C_metar_date + " integer, "
            + C_taf + " text, "
            + C_taf_date + " integer, "
            + C_notam + " text, "
            + C_notam_date + " integer, "
            + C_notam_position + " text, "
            + C_notam_polygon + " text, "
            + C_notam_number + " text, "
            + C_latest + " integer"
            + " );";

    private static final String AIRPORTINFO_IDENT_INDEX = "create index airportinfo_ident_index" +
            " on " + AIRPORTINFO_TABLE_NAME + " (" + C_ident + ");";
    private static final String AIRPORTINFO_METARDATE_INDEX = "create index airportinfo_metardate_index" +
            " on " + AIRPORTINFO_TABLE_NAME + " (" + C_metar_date + ");";
    private static final String AIRPORTINFO_TAFDATE_INDEX = "create index airportinfo_tafdate_index" +
            " on " + AIRPORTINFO_TABLE_NAME + " (" + C_taf_date + ");";
    private static final String AIRPORTINFO_NOTAMDATE_INDEX = "create index airportinfo_notamdate_index" +
            " on " + AIRPORTINFO_TABLE_NAME + " (" + C_notam_date + ");";
    private static final String AIRPORTINFO_NOTAMNUMBER_INDEX = "create index airportinfo_notamnumber_index" +
            " on " + AIRPORTINFO_TABLE_NAME + " (" + C_notam_number + ");";

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

    private static UserDBHelper sInstance;
    public static synchronized UserDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new UserDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
        DbPath = this.myContext.getApplicationInfo().dataDir + "/databases/";
    }

    private Context myContext;
    private String DbPath;

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
        Log.i(TAG, "Creating Database table AirportInfo");
        db.execSQL(AIRPORTINFO_TABLE);
        Log.i(TAG, "Creating Index AirportInfo Ident");
        db.execSQL(AIRPORTINFO_IDENT_INDEX);
        Log.i(TAG, "Creating Index AirportInfo metardate");
        db.execSQL(AIRPORTINFO_METARDATE_INDEX);
        Log.i(TAG, "Creating Index AirportInfo tafdate");
        db.execSQL(AIRPORTINFO_TAFDATE_INDEX);
        Log.i(TAG, "Creating Index AirportInfo notamdate");
        db.execSQL(AIRPORTINFO_NOTAMDATE_INDEX);
        Log.i(TAG, "Creating Index AirportInfo notamnumber");
        db.execSQL(AIRPORTINFO_NOTAMNUMBER_INDEX);
        Log.i(TAG, "Created AirportCharts table");
        db.execSQL(AIRPORTCHARTS_TABLE);
        Log.i(TAG, "Creating MBTilesLocal table");
        db.execSQL(MBTILES_LOCAL_TABLE);
    }

    private void insertBasicPropertiesData(SQLiteDatabase db)
    {
        String i = "INSERT INTO " + PROPERTIES_TABLE_NAME + " (" +
                C_name + ", " + C_value1 + ", " + C_value2 + ") VALUES(";
        db.execSQL(i + "'INIT_POSITION', '52.453917', '5.515624');");
        db.execSQL(i + "'INIT_ZOOM', '8', '');");
        db.execSQL(i + "'INIT_FLIGHTPLAN_ID', '0', '');");
        db.execSQL(i + "'DB_VERSION', '20180528', '2018-05-28');");
        db.execSQL(i + "'SERVER', '192.168.2.8', '81');");
        db.execSQL(i + "'INIT_AIRPORT', '2522', '237920,le');");
        db.execSQL(i + "'INSTRUMENTS', 'visible', '1');");
        db.execSQL(i + "'LOCATION', 'Provider', 'simV2');");
        db.execSQL(i + "'MARKERS', 'visible', 'test');");
        db.execSQL(i + "'BUFFER', '0.3', 'true');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Updating UserDB Database from version:" + oldVersion + " to version:" + newVersion);
        Toast.makeText(myContext, "Updating userdata database.", Toast.LENGTH_LONG).show();

        // add columns to flightplan table
        if (oldVersion<3) db.execSQL(FLIGHTPLAN_TABLE_ADD_DATE);

        if (oldVersion<4) {
            String i = "INSERT INTO " + PROPERTIES_TABLE_NAME + " (" +
                    C_name + ", " + C_value1 + ", " + C_value2 + ") VALUES(";
            db.execSQL(i + "'MARKERS', 'visible', 'test');");
        }

        if (oldVersion<9){
            db.execSQL("drop table if exists " + AIRPORTINFO_TABLE_NAME + ";");
            Log.i(TAG, "Creating Database table AirportInfo");
            db.execSQL(AIRPORTINFO_TABLE);
            Log.i(TAG, "Creating Index AirportInfo Ident");
            db.execSQL(AIRPORTINFO_IDENT_INDEX);
            Log.i(TAG, "Creating Index AirportInfo metardate");
            db.execSQL(AIRPORTINFO_METARDATE_INDEX);
            Log.i(TAG, "Creating Index AirportInfo tafdate");
            db.execSQL(AIRPORTINFO_TAFDATE_INDEX);
            Log.i(TAG, "Creating Index AirportInfo notamdate");
            db.execSQL(AIRPORTINFO_NOTAMDATE_INDEX);
            Log.i(TAG, "Creating Index AirportInfo notamnumber");
            db.execSQL(AIRPORTINFO_NOTAMNUMBER_INDEX);

            String i = "INSERT INTO " + PROPERTIES_TABLE_NAME + " (" +
                    C_name + ", " + C_value1 + ", " + C_value2 + ") VALUES(";
            db.execSQL(i + "'BUFFER', '0.3', 'true');");
        }

        if (oldVersion<10){
            db.execSQL(AIRPORTCHARTS_TABLE);
            Log.i(TAG, "Created AirportCharts table");
        }

        if (oldVersion<13)
        {
            try {
                db.execSQL(MBTILES_LOCAL_TABLE);
                Log.i(TAG, "Create MBTilesLocal Table");
            }
            catch(Exception ee)
            {
                Log.e(TAG, ee.getMessage());
            }
        }

        if (oldVersion<15)
        {
            db.execSQL("ALTER TABLE " + MBTILES_LOCAL_TABLE_NAME + " ADD COLUMN " + C_local_file + " text;");
        }

        if (oldVersion<17)
        {
            db.execSQL("UPDATE " + UserDBHelper.PROPERTIES_TABLE_NAME + " SET value2='simv2' WHERE name='LOCATION' AND value2='sim';");
        }

        updated = true;
    }

    private void insertStandaardChartsData(SQLiteDatabase db)
    {
        String i = "INSERT INTO " + AIRPORTCHARTS_TABLE_NAME + " (" +
                 C_airport_id + ", "
                + C_name + ", "
                + C_url + ", "
                + C_thumbnail_url + ", "
                + C_version + ", "
                + C_created_date + ", "
                + C_active + ") VALUES("
                + " );";
    }
}

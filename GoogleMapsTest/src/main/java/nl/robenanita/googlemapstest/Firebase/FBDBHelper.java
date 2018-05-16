package nl.robenanita.googlemapstest.Firebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import nl.robenanita.googlemapstest.R;

public class FBDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "fbairnav.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "GooglemapsTest";

    public static final String AIRPORT_TABLE_NAME = "tbl_Airports";
    public static final String COUNTRY_TABLE_NAME = "tbl_Country";
    public static final String CONTINENT_TABLE_NAME = "tbl_Continent";
    public static final String REGIONS_TABLE_NAME = "tbl_Region";
    public static final String RUNWAY_TABLE_NAME = "tbl_Runways";
    public static final String FIXES_TABLE_NAME = "tbl_Fixes";
    public static final String FIRS_TABLE_NAME = "tbl_Firs";
    public static final String NAVAIDS_TABLE_NAME = "tbl_Navaids";
    public static final String PROPERTIES_TABLE_NAME = "tbl_Properties";
    public static final String FREQUENCIES_TABLE_NAME = "tbl_AirportFrequencies";
    public static final String MBTILES_TABLE_NAME = "tbl_MbTiles";

    public static final String C_id = "id";
    public static final String C_ident = "ident";
    public static final String C_type = "type";
    public static final String C_name = "name";
    public static final String C_latitude_deg = "latitude_deg";
    public static final String C_longitude_deg = "longitude_deg";
    public static final String C_elevation_ft = "elevation_ft";
    public static final String C_continent = "continent";
    public static final String C_iso_country = "iso_country";
    public static final String C_iso_region = "iso_region";
    public static final String C_municipality = "municipality";
    public static final String C_scheduled_service = "scheduled_service";
    public static final String C_gps_code = "gps_code";
    public static final String C_iata_code = "iata_code";
    public static final String C_local_code = "local_code";
    public static final String C_home_link = "home_link";
    public static final String C_wikipedia_link = "wikipedia_link";
    public static final String C_keywords = "keywords";
    public static final String C_Version = "Version";
    public static final String C_Modified = "Modified";

    public static final String C_code = "code";
    public static final String C_heading = "heading";

    public static final String C_frequency = "frequency_khz";

    public static final String C_airport_ident = "airport_ident";
    public static final String C_airport_ref = "airport_ref";
    public static final String C_length = "length_ft";
    public static final String C_width = "width_ft";
    public static final String C_surface = "surface";
    public static final String C_le_ident = "le_ident";
    public static final String C_le_latitude = "le_latitude_deg";
    public static final String C_le_longitude = "le_longitude_deg";
    public static final String C_le_elevation = "le_elevation_ft";
    public static final String C_le_heading = "le_heading_degT";
    public static final String C_le_displaced_threshold = "le_displaced_threshold_ft";
    public static final String C_he_ident = "he_ident";
    public static final String C_he_latitude = "he_latitude_deg";
    public static final String C_he_longitude = "he_longitude_deg";
    public static final String C_he_elevation = "he_elevation_ft";
    public static final String C_he_heading = "he_heading_degT";
    public static final String C_he_displaced_threshold = "he_displaced_threshold_ft";
    public static final String C_lighted = "lighted";
    public static final String C_closed = "closed";

    public static final String C_filename = "filename";
    public static final String C_dme_frequency_khz = "dme_frequency_khz";
    public static final String C_dme_channel = "dme_channel";
    public static final String C_dme_latitude_deg = "dme_latitude_deg";
    public static final String C_dme_longitude_deg = "dme_longitude_deg";
    public static final String C_dme_elevation_ft = "dme_elevation_ft";
    public static final String C_slaved_variation_deg = "slaved_variation_deg";
    public static final String C_magnetic_variation_deg = "magnetic_variation_deg";
    public static final String C_usageType = "usageType";
    public static final String C_power = "power";
    public static final String C_associated_airport = "associated_airport";
    public static final String C_associated_airport_id = "associated_airport_id";

    public static final String C_description = "description";
    public static final String C_frequency_mhz = "frequency_mhz";

    public static final String C_mapLocation_ID = "MapLocation_ID";
    public static final String C_pid = "pid";

    public static final String C_mbtileslink = "mbtileslink";
    public static final String C_xmllink = "xmllink";
    public static final String C_version = "version";
    public static final String C_startValidity = "startValidity";
    public static final String C_endValidity = "endValidity";
    public static final String C_region = "region";

    public static final String C_position = "position";
    public static final String C_polygon = "polygon";

    public static final String C_value1 = "value1";
    public static final String C_value2 = "value2";

    private Context context;

    public static SQLiteDatabase database;

    private static FBDBHelper sInstance;
    public static synchronized FBDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new FBDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private FBDBHelper(Context context) {
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
        // Create Tables
        String q = context.getString(R.string.createAirportsTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createFrequenciesTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createRunwaysTable);
        sqLiteDatabase.execSQL(q);

        // Add Indexes
        q = context.getString(R.string.createNameIdentAirportTableIndex);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createLocationAirportTableIndex);
        sqLiteDatabase.execSQL(q);

        q = context.getString(R.string.createRunwaysAirportIdentIndex);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createRunwaysAirportHELocationIndex);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createRunwaysAirportLELocationIndex);
        sqLiteDatabase.execSQL(q);

        q = context.getString(R.string.createNavaidsTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createNavaidsIdentNameIndex);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createNavaidsLocationIndex);
        sqLiteDatabase.execSQL(q);

        q = context.getString(R.string.createContinentTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createCountryTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createMbTilesTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createFirsTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createFixesTable);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createFixesLocationIndex);
        sqLiteDatabase.execSQL(q);
        q = context.getString(R.string.createFixesNameIndex);
        sqLiteDatabase.execSQL(q);

        q = context.getString(R.string.createRegionsTable);
        sqLiteDatabase.execSQL(q);

        q = context.getString(R.string.createPropertiesTable);
        sqLiteDatabase.execSQL(q);
        q = "INSERT INTO " + PROPERTIES_TABLE_NAME + " (" +
                C_name + ", " + C_value1 + ", " + C_value2 + ") VALUES(";
        sqLiteDatabase.execSQL(q + "'DB_VERSION', '20180515', '2018-05-15');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.i(TAG, "Updating database from version: " + oldVersion + " to version: " + newVersion);
        if (oldVersion<2)
        {
            String q = context.getString(R.string.createRegionsTable);
            sqLiteDatabase.execSQL(q);

            q = context.getString(R.string.createPropertiesTable);
            sqLiteDatabase.execSQL(q);

            q = "INSERT INTO " + PROPERTIES_TABLE_NAME + " (" +
                    C_name + ", " + C_value1 + ", " + C_value2 + ") VALUES(";
            sqLiteDatabase.execSQL(q + "'DB_VERSION', '20180515', '2018-05-15');");
        }
    }
}

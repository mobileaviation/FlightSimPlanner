package nl.robenanita.googlemapstest.spatialdb;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jsqlite.Constants;
import jsqlite.Database;
import jsqlite.Stmt;
import jsqlite.TableResult;

/**
 * Created by Rob Verhoef on 18-3-2015.
 */
public class SpatialHelper {
    private static String DB_PATH = "/data/data/nl.robenanita.googlemapstest/databases/";
    private Database database;
    private Context context;

    private static final String OLD_DATABASE_NAME = "airnav.db";
    private static final String DATABASE_NAME = "airnav_v2.db";

    public static final String AIRPORT_TABLE_NAME = "tbl_Airports";
    public static final String COUNTRY_TABLE_NAME = "tbl_Country";
    public static final String CONTINENT_TABLE_NAME = "tbl_Continent";
    public static final String RUNWAY_TABLE_NAME = "tbl_Runways";
    public static final String FIXES_TABLE_NAME = "tbl_Fixes";
    public static final String NAVAIDS_TABLE_NAME = "tbl_Navaids";
    public static final String PROPERTIES_TABLE_NAME = "tbl_Properties";
    public static final String FREQUENCIES_TABLE_NAME = "tbl_AirportFrequencies";

    private static final String TAG = "GooglemapsTest";

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

    public static final String C_description = "description";
    public static final String C_frequency_mhz = "frequency_mhz";

    public static final String C_mapLocation_ID = "MapLocation_ID";
    public static final String C_pid = "pid";

    public SpatialHelper(Context context)
    {
        Log.i(TAG, "Creating spatial database Helper class");
        this.context = context;
        database = new Database();

        if (deleteDatabase())
        {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i(TAG, "Copied new database: " + DATABASE_NAME);
            }
        }
    }

    public Database openDatabase()
    {
        String myPath = DB_PATH + DATABASE_NAME;
        try {
            database.open(myPath, Constants.SQLITE_OPEN_READWRITE
                    | Constants.SQLITE_OPEN_CREATE);
            Log.i(TAG, "Opened database succesfully");
        } catch (jsqlite.Exception e) {
            Log.e(TAG, "Open database error: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            return database;
        }
    }

    public void closeDatabase()
    {
        try {
            database.close();
            Log.i(TAG, "Closed database succesfully");
        } catch (jsqlite.Exception e) {
            Log.e(TAG, "Close database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testDatabase()
    {
        Log.i(TAG, "Check versions...\n");

        try {
            Stmt stmt01 = database.prepare("SELECT spatialite_version();");
            if (stmt01.step()) {
                Log.i(TAG, "SPATIALITE_VERSION: " + stmt01.column_string(0));
            }

            stmt01 = database.prepare("SELECT proj4_version();");
            if (stmt01.step()) {
                Log.i(TAG, "PROJ4_VERSION: " + stmt01.column_string(0));
            }

            stmt01 = database.prepare("SELECT geos_version();");
            if (stmt01.step()) {
                Log.i(TAG, "GEOS_VERSION: " + stmt01.column_string(0));
            }
            stmt01.close();

            //database.exec("SELECT * FROM tbl_Continent;", new Callback() {
            TableResult t = database.get_table("SELECT * FROM tbl_Continent;");

            Log.i(TAG, "Tests finished");

        } catch (jsqlite.Exception e) {
            Log.e(TAG, "database test functions error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean deleteDatabase()
    {
//        String myPath = DB_PATH + OLD_DATABASE_NAME;
//        File f = new File(myPath);
//
//        if (f.canRead())
            return context.deleteDatabase(OLD_DATABASE_NAME);
       // else return false;
    }

    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = null;
        OutputStream myOutput = null;


        try
        {
            myInput = context.getAssets().open(DATABASE_NAME);
            // Path to the just created empty db
            String outFileName = DB_PATH + DATABASE_NAME;
            //Open the empty db as the output stream
            myOutput = new FileOutputStream(outFileName);
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }
        }
        catch(Exception ee)
        {
            Log.e(TAG, ee.getMessage());
        }
        finally {
            if (myOutput != null) myOutput.flush();
            if (myOutput != null) myOutput.close();
            if (myInput != null) myInput.close();
        }
    }
}

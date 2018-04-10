package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rob Verhoef on 18-1-14.
 */
public class DBHelper extends SQLiteOpenHelper {
    //The Android's default system path of your application database.

    public static String DB_PATH = "/data/data/nl.robenanita.googlemapstest/databases/";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public static final String DATABASE_NAME = "airnav.db";

    public static final String AIRPORT_TABLE_NAME = "tbl_Airports";
    public static final String COUNTRY_TABLE_NAME = "tbl_Country";
    public static final String CONTINENT_TABLE_NAME = "tbl_Continent";
    public static final String RUNWAY_TABLE_NAME = "tbl_Runways";
    public static final String FIXES_TABLE_NAME = "tbl_Fixes";
    public static final String NAVAIDS_TABLE_NAME = "tbl_Navaids";
    public static final String PROPERTIES_TABLE_NAME = "tbl_Properties";
    public static final String FREQUENCIES_TABLE_NAME = "tbl_AirportFrequencies";
    public static final String MBTILES_TABLE_NAME = "tbl_MbTiles";

    private static final int DATABASE_VERSION = 23;
    private static final String TAG = "GooglemapsTest";

    public Boolean updated = false;

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

    public static String DbPath;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DbPath = this.myContext.getApplicationInfo().dataDir + "/databases/";
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            Log.i(TAG, "Creating Database");
            //createDataBase();
        }
        catch (Exception ee)
        {
            Log.e(TAG, "Error Creating DB " + ee.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try
        {

            Log.i(TAG, "Updating Database from version:" + oldVersion + " to version:" + newVersion);
            copyDataBase();
            //createDataBase();
        }
        catch(Exception ee)
        {
            Log.e(TAG, "Error Updating DB " + ee.getMessage());
        }
    }

    private SQLiteDatabase checkDB;
    private boolean checkDataBase(){
        checkDB = null;
        try{
            String myPath = DbPath + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch(SQLiteException e){
            //database does't exist yet.
            Log.e(TAG, "Check DB Error");
        }

        if(checkDB != null){
            checkDB.close();
            return true;
        }
        else return  false;
    }

    public void createDataBase() throws IOException{
        // Empty for compatibility
    }

    private void copyDataBase() throws Exception{
        //Open your local db as the input stream
        InputStream myInput = null;
        OutputStream myOutput = null;


        try
        {
            myInput = myContext.getAssets().open(DATABASE_NAME);
            // Path to the just created empty db
            String outFileName = DbPath + DATABASE_NAME;
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
            if (myOutput != null) {
                myOutput.flush();
                myOutput.close();
            }
            if (myInput != null) myInput.close();
        }
    }

    public SQLiteDatabase openDataBase(){
        //Open the database
        if (!checkDataBase()) {
            this.getWritableDatabase();
            try {
                copyDataBase();
            }
            catch (Exception ee)
            {
                Log.e(TAG, "Error copy database: " + ee.getMessage());
                return null;
            }
        }

        String myPath = DbPath + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        return myDataBase;
    }

    public void deleteDatabase(Integer version)
    {
        Log.i(TAG, "Try to remove the old database: " + version.toString() + " : " + this.DATABASE_VERSION);
        if (version != this.DATABASE_VERSION)
            {
            //this.close();
            Log.i(TAG, "Try to remove the old database");
            try
            {
                myContext.deleteDatabase("airnav.db");
                Log.i(TAG, "tried to delete database: airnav.db" );
            }
            catch (Exception ee)
            {
                Log.i(TAG, "Problem deleting database: " + ee.getMessage());
            }
        }
    }
}

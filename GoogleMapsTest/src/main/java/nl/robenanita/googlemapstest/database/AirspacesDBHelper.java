package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AirspacesDBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public static final String DATABASE_NAME = "all_airspaces.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "GooglemapsTest";

    public static String DbPath;

    public AirspacesDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DbPath = this.myContext.getApplicationInfo().dataDir + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try
        {
            Log.i(TAG, "Updating Database from version:" + oldVersion + " to version:" + newVersion);
            DBFilesHelper.CopyFromAssetDatabaseTo(myContext, DATABASE_NAME, DbPath);
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

    public SQLiteDatabase openDataBase(){
        //Open the database
        if (!checkDataBase()) {
            this.getWritableDatabase();
            try {
                DBFilesHelper.CopyFromAssetDatabaseTo(myContext, DATABASE_NAME, DbPath);
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

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }
}

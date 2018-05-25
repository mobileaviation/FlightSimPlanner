package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AirspacesDBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public static final String DATABASE_NAME = "fbairspaces.db";
    //public static final String DATABASE_NAME = "all_airspaces.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "GooglemapsTest";

    public static String DbPath;

    public AirspacesDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase openDataBase(){
        return this.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }
}

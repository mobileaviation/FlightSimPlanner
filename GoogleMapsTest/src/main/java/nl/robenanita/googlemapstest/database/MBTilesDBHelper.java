package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MBTilesDBHelper extends SQLiteOpenHelper {

    private static Integer DatabaseVersion = 1;
    private Context myContext;

    public MBTilesDBHelper(Context context, String databaseName)
    {
        super(context, databaseName, null, DatabaseVersion);
        myContext = context;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    public String getDBPath()
    {
        return this.myContext.getApplicationInfo().dataDir + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

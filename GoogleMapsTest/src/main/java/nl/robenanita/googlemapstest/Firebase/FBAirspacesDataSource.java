package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FBAirspacesDataSource {
    public FBAirspacesDataSource(Context context)
    {
        this.context = context;
        fbAirspacesDBHelper = FBAirspacesDBHelper.getInstance(context);
    }

    private static final String TAG = "GooglemapsTest";
    private Context context;
    private DatabaseReference mDatabase;
    private FBAirspacesDBHelper fbAirspacesDBHelper;
    private SQLiteDatabase database;

    public FBTableDownloadProgress progress;

    public void Open()
    {
        database = fbAirspacesDBHelper.Open();
    }

    public void Close()
    {
        fbAirspacesDBHelper.close();
    }

    Integer start;
    Integer count;
    Query query;
    ValueEventListener dataListener;

    public void ReadFBAirspacesData(final Integer airspacesCount, Boolean clearTable)
    {
        if (clearTable) deleteAllRowsFromTables();

        FirebaseOptions fbOptions = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBII0CEWciBmPSy7xceMpt8Znc6DrAOkF4")
                .setApplicationId("1:673977020964:android:9c9afb207af7615d")
                .setDatabaseUrl("https://flightsimplannerairspaces.firebaseio.com")
                .build();
        final FirebaseApp airspacesApp = FirebaseApp.initializeApp(context, fbOptions, "airspaces");
        mDatabase = FirebaseDatabase.getInstance(airspacesApp).getReference();

        start = 0;
        count = 1000;

        query = mDatabase.child("airspaces").orderByChild("index").startAt(start).endAt(start + (count-1));

        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot airspaceSnapshow: dataSnapshot.getChildren()){
                        FBAirspace airspace = airspaceSnapshow.getValue(FBAirspace.class);
                        ContentValues v = airspace.getAirspaceContentValues();
                        database.insert(FBAirspacesDBHelper.AIRSPACES_TABLE_NAME, null, v);
                    }

                    start = start + count;
                    Log.i(TAG, "Read 1000 airspaces, get the next from: " + start.toString());
                    if (progress != null) progress.onProgress(airspacesCount, start, FBTableType.airspaces);

                    query = mDatabase.child("airspaces").orderByChild("index").startAt(start).endAt(start + (count-1));
                    if (start<airspacesCount) query.addListenerForSingleValueEvent(dataListener);
                    else {
                        if (progress != null) progress.onProgress(airspacesCount, airspacesCount, FBTableType.airspaces);
                        Log.i(TAG, "Finished reading airspaces");
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    }
                }
                catch (Exception ee)
                {
                    Log.i("Test", ee.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //database.beginTransaction();
        query.addListenerForSingleValueEvent(dataListener);
    }

    private void deleteAllRowsFromTables()
    {
        database.execSQL("DELETE FROM " + FBAirspacesDBHelper.AIRSPACES_TABLE_NAME);
    }
}

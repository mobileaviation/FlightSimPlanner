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

public class FBFixesDataSource {
    public FBFixesDataSource(Context context)
    {
        this.context = context;
        fbdbHelper = FBDBHelper.getInstance(context);
    }

    private static final String TAG = "GooglemapsTest";

    private Context context;
    private FBDBHelper fbdbHelper;
    private SQLiteDatabase database;
    private DatabaseReference mDatabase;

    public FBTableDownloadProgress progress;

    public void Open()
    {
        database = fbdbHelper.Open();
    }

    public void Close()
    {
        fbdbHelper.close();
    }

    Integer start;
    Integer count;
    Query query;
    ValueEventListener dataListener;

    public void ReadFBFixesData(final Integer fixesCount, Boolean clearTable)
    {
        if (clearTable) deleteAllRowsFromTables();

        FirebaseOptions fbOptions = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAZoA41QRDAsIV7zQ8ZqM0JLDpX8aQ88-E")
                .setApplicationId("1:702285397223:android:9c9afb207af7615d")
                .setDatabaseUrl("https://flightsimplanner-202711.firebaseio.com")
                .build();
        FirebaseApp fixesApp = FirebaseApp.initializeApp(context, fbOptions, "fixes");
        mDatabase = FirebaseDatabase.getInstance(fixesApp).getReference();

        start = 0;
        count = 1000;

        query = mDatabase.child("fixes").orderByChild("index").startAt(start).endAt(start + (count-1));

        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //GenericTypeIndicator<List<Object>> airportsType = new GenericTypeIndicator<List<Object>>(){};
                    for (DataSnapshot fixSnapshow: dataSnapshot.getChildren()){
                        FBFix fix = fixSnapshow.getValue(FBFix.class);
                        ContentValues v = fix.getFixContentValues();
                        database.insert(FBDBHelper.FIXES_TABLE_NAME, null, v);
                    }

                    start = start + count;
                    Log.i(TAG, "Read 1000 fixes, get the next from: " + start.toString());
                    if (progress != null) progress.onProgress(fixesCount, start, FBTableType.fixes);

                    query = mDatabase.child("fixes").orderByChild("index").startAt(start).endAt(start + (count-1));
                    if (start<fixesCount) query.addListenerForSingleValueEvent(dataListener);
                    else {
                        if (progress != null) progress.OnFinished(FBTableType.fixes);
                        Log.i(TAG, "Finished reading fixes");
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

        database.beginTransaction();
        query.addListenerForSingleValueEvent(dataListener);
    }

    private void deleteAllRowsFromTables()
    {
        database.execSQL("DELETE FROM " + FBDBHelper.FIXES_TABLE_NAME);
    }
}

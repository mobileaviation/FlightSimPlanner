package nl.robenanita.googlemapstest.Firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FBTilesDataSource {
    public FBTilesDataSource(Context context)
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

    public void ReadFBTilesData(final Integer tilesCount, Boolean clearTable)
    {
        if (clearTable) deleteAllRowsFromTables();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        start = 0;
        count = 1000;

        query = mDatabase.child("tiles").orderByChild("index").startAt(start).endAt(start + (count-1));

        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //GenericTypeIndicator<List<Object>> airportsType = new GenericTypeIndicator<List<Object>>(){};
                    for (DataSnapshot tileSnapshow: dataSnapshot.getChildren()){
                        FBTile tile = tileSnapshow.getValue(FBTile.class);
                        ContentValues v = tile.getTileContentValues();
                        database.insert(FBDBHelper.MBTILES_TABLE_NAME, null, v);
                    }

                    start = start + count;
                    Log.i(TAG, "Read 1000 tiles, get the next from: " + start.toString());
                    if (progress != null) progress.onProgress(tilesCount, start, FBTableType.mbtiles);

                    query = mDatabase.child("tiles").orderByChild("index").startAt(start).endAt(start + (count-1));
                    if (start<tilesCount) query.addListenerForSingleValueEvent(dataListener);
                    else {
                        if (progress != null) progress.onProgress(tilesCount, tilesCount, FBTableType.mbtiles);
                        Log.i(TAG, "Finished reading tiles");
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
        database.execSQL("DELETE FROM " + FBDBHelper.MBTILES_TABLE_NAME);
    }
}

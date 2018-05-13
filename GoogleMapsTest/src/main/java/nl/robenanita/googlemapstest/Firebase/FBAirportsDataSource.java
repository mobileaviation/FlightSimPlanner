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

import java.util.ArrayList;

public class FBAirportsDataSource {
    public FBAirportsDataSource(Context context)
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
    public void ReadFBAirportData(final Integer airportCount, Boolean clearTable)
    {
        if (clearTable) deleteAllRowsFromTables();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        start = 0;
        count = 1000;

        query = mDatabase.child("airports").orderByChild("index").startAt(start).endAt(start + (count-1));

        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //GenericTypeIndicator<List<Object>> airportsType = new GenericTypeIndicator<List<Object>>(){};
                    for (DataSnapshot airportSnapshow: dataSnapshot.getChildren()){
                        FBAirport airport = airportSnapshow.getValue(FBAirport.class);
                        ContentValues v = airport.getAirportContentValues();
                        database.insert(FBDBHelper.AIRPORT_TABLE_NAME, null, v);

                        if (airport.runways != null) {
                            for (FBRunway runway : airport.runways) {
                                ContentValues r = runway.getRunwayValues();
                                database.insert(FBDBHelper.RUNWAY_TABLE_NAME, null, r);
                            }
                        }

                        if (airport.frequencies != null) {
                            for (FBFrequency frequency : airport.frequencies) {
                                ContentValues f = frequency.getFrequencyValues();
                                database.insert(FBDBHelper.FREQUENCIES_TABLE_NAME, null, f);
                            }
                        }
                    }

                    start = start + count;
                    Log.i(TAG, "Read 1000 airports, get the next from: " + start.toString());
                    if (progress != null) progress.onProgress(airportCount, start, FBTableType.airports);

                    query = mDatabase.child("airports").orderByChild("index").startAt(start).endAt(start + (count-1));
                    if (start<airportCount) query.addListenerForSingleValueEvent(dataListener);
                        else {
                        if (progress != null) progress.onProgress(airportCount, airportCount, FBTableType.airports);
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        Log.i(TAG, "Finished reading airports");
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
        database.execSQL("DELETE FROM " + FBDBHelper.AIRPORT_TABLE_NAME);
        database.execSQL("DELETE FROM " + FBDBHelper.RUNWAY_TABLE_NAME);
        database.execSQL("DELETE FROM " + FBDBHelper.FREQUENCIES_TABLE_NAME);
    }


}


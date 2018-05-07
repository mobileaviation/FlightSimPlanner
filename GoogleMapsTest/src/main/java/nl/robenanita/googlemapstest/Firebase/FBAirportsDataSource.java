package nl.robenanita.googlemapstest.Firebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FBAirportsDataSource {
    public FBAirportsDataSource(Context context)
    {
        this.context = context;
        fbdbHelper = FBDBHelper.getInstance(context);
        airports = new ArrayList<>();
    }

    private static final String TAG = "GooglemapsTest";

    private Context context;
    private FBDBHelper fbdbHelper;
    private SQLiteDatabase database;
    private DatabaseReference mDatabase;

    private ArrayList<FBAirport> airports;

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
    public void ReadFBDataTest(final Integer airportCount)
    {
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
                        airports.add(airport);
                        //Log.i(TAG, "retrieved Object: " + airport.name);
                    }

                    start = start + count;
                    Log.i(TAG, "Read 1000 airports, get the next from: " + start.toString());

                    query = mDatabase.child("airports").orderByChild("index").startAt(start).endAt(start + (count-1));
                    if (start<airportCount) query.addListenerForSingleValueEvent(dataListener);
                        else
                            Log.i(TAG, "Finished reading airports");
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

        query.addListenerForSingleValueEvent(dataListener);
    }


}


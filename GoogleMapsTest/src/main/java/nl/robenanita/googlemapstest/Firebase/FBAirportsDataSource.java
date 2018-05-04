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
    }

    private static final String TAG = "GooglemapsTest";

    private Context context;
    private FBDBHelper fbdbHelper;
    private SQLiteDatabase database;
    private DatabaseReference mDatabase;

    public void Open()
    {
        database = fbdbHelper.Open();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void Close()
    {
        fbdbHelper.close();
    }

    public void ReadFBDataTest()
    {
        Query query = mDatabase.child("airports").orderByChild("index").startAt(0).endAt(1000);

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //GenericTypeIndicator<List<Object>> airportsType = new GenericTypeIndicator<List<Object>>(){};
                    Object airports = dataSnapshot.getValue();
                    Log.i(TAG, "retrieved Object");
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

        query.addValueEventListener(dataListener);
    }


}


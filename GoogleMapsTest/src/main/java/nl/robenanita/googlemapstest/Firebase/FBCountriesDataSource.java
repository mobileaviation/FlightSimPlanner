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

public class FBCountriesDataSource {
    public FBCountriesDataSource(Context context)
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
    }

    public void Close()
    {
        fbdbHelper.close();
    }

    Integer start;
    Integer count;
    Query query;
    ValueEventListener dataListener;

    public void ReadFBCountryData(final Integer countryCount)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        start = 0;
        count = 100;

        query = mDatabase.child("countries").orderByChild("index").startAt(start).endAt(start + (count-1));

        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //GenericTypeIndicator<List<Object>> airportsType = new GenericTypeIndicator<List<Object>>(){};
                    for (DataSnapshot countrySnapshow: dataSnapshot.getChildren()){
                        FBCountry country = countrySnapshow.getValue(FBCountry.class);
                        ContentValues v = country.getCountryContentValues();
                        database.insert(FBDBHelper.COUNTRY_TABLE_NAME, null, v);
                    }

                    start = start + count;
                    Log.i(TAG, "Read 100 countries, get the next from: " + start.toString());

                    query = mDatabase.child("counrties").orderByChild("index").startAt(start).endAt(start + (count-1));
                    if (start<countryCount) query.addListenerForSingleValueEvent(dataListener);
                    else {
                        Log.i(TAG, "Finished reading countries");
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
}

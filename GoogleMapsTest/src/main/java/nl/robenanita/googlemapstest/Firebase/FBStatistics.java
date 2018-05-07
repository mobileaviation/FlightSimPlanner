package nl.robenanita.googlemapstest.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FBStatistics {
    public FBStatistics()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public interface StatisticsEventListerner
    {
        public void OnStatistics(FBStatistics statistics);
    }

    public StatisticsEventListerner OnStatisticsEvent;

    private DatabaseReference mDatabase;

    public Integer AirportsCount;
    public Integer CountriesCount;
    public Integer FixesCount;
    public Integer MBTilesCount;
    public Integer NavaidsCount;
    public Integer PropertiesCount;
    public Integer RegionsCount;

    public void FillStatistics()
    {
        Query statisticsQuery = mDatabase.child("statistics");

        statisticsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FBStatistics stats = dataSnapshot.getValue(FBStatistics.class);
                if (OnStatisticsEvent != null) OnStatisticsEvent.OnStatistics(stats);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

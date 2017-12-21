package nl.robenanita.googlemapstest;

import android.content.Context;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;

/**
 * Created by Rob Verhoef on 21-5-2014.
 */
public class LocationTracking {
    public LocationTracking(Route flightPlan, Context context)
    {
        this.flightPlan = flightPlan;
        this.context = context;

        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (this.flightPlan != null) Name = currentDateandTime + " : " + this.flightPlan.name;
        else Name = currentDateandTime;

        insertDatabase();
    }

    private void insertDatabase()
    {
        LocationTrackingDataSource locationTrackingDataSource = new LocationTrackingDataSource(context);
        locationTrackingDataSource.open();
        Id = locationTrackingDataSource.insertNewLocationTracking(this);
        locationTrackingDataSource.close();
    }

    public void SetLocationPoint(Location location)
    {
        LocationTrackingDataSource locationTrackingDataSource = new LocationTrackingDataSource(context);
        locationTrackingDataSource.open();
        locationTrackingDataSource.setTrackPoint(this, location);
        locationTrackingDataSource.close();
    }

    private Context context;
    private Route flightPlan;
    public String Name;
    public Long Id;
}

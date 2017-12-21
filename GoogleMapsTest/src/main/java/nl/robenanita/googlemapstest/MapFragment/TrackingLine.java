package nl.robenanita.googlemapstest.MapFragment;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import nl.robenanita.googlemapstest.LocationTracking;
import nl.robenanita.googlemapstest.Route.Route;

/**
 * Created by Rob Verhoef on 30-7-2017.
 */

public class TrackingLine {
    public TrackingLine(GoogleMap googleMap, Route flightPlan, Context context)
    {
        this.googleMap = googleMap;
        this.locationTracking = new LocationTracking(flightPlan, context);
    }

    private GoogleMap googleMap;
    private Route flightPlan;
    private LocationTracking locationTracking;

    private LatLng oldPoint;
    private float pointDistance = 0;

    public float SetTrackPoints(Location newPoint)
    {
        float b = newPoint.getBearing();
        if (oldPoint != null)
        {
            Location loc = new Location("loc old");
            loc.setLatitude(oldPoint.latitude);
            loc.setLongitude(oldPoint.longitude);

            Location locnew = new Location("loc new");
            locnew.setLatitude(newPoint.getLatitude());
            locnew.setLongitude(newPoint.getLongitude());

            float v = loc.distanceTo(locnew);

            if (v>100)
            {
                b = loc.bearingTo(locnew);

                PolylineOptions trackOptions = new PolylineOptions();
                trackOptions.color(Color.GREEN);
                trackOptions.width(5);
                trackOptions.zIndex(1000);
                trackOptions.add(oldPoint);
                trackOptions.add(new LatLng(newPoint.getLatitude(), newPoint.getLongitude()));
                oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
                googleMap.addPolyline(trackOptions);

                if (locationTracking != null)
                    locationTracking.SetLocationPoint(newPoint);
            }
        }
        else
        {
            oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
        }

        return b;
    }
}

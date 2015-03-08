package nl.robenanita.googlemapstest.Tracks;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;

/**
 * Created by Rob Verhoef on 25-5-2014.
 */
public class LoadTrack {
    private ArrayList<LocationTrackingDataSource.TrackPoint> trackPoints;
    public ArrayList<LocationTrackingDataSource.TrackPoint> getTrackPoints() { return  trackPoints; }
    public LoadTrack(Context c, GoogleMap map, Integer track_id)
    {
        this.map = map;
        this.track_id = track_id;
        this.trackLine = null;

        LocationTrackingDataSource locationTrackingDataSource = new LocationTrackingDataSource(c);
        locationTrackingDataSource.open();
        trackPoints = locationTrackingDataSource.getTrackPoints(this.track_id);
        locationTrackingDataSource.close();


        LatLng newPoint;
        trackOptions = new PolylineOptions();
        trackOptions.color(Color.YELLOW);
        trackOptions.width(5);

        for (LocationTrackingDataSource.TrackPoint tp : trackPoints)
        {
            newPoint = new LatLng(tp.latitude, tp.longitude);
            trackOptions.add(newPoint);
        }

        trackLine = this.map.addPolyline(trackOptions);
    }

    public void removeTrack()
    {
        if (trackLine != null)
            trackLine.remove();
        trackLine = null;
    }

    private GoogleMap map;
    private Integer track_id;
    private PolylineOptions trackOptions;
    private Polyline trackLine;
}

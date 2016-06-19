package nl.robenanita.googlemapstest.markers;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 17-9-2015.
 */
public class PlaneMarker {
    public PlaneMarker(GoogleMap map, LatLng position, Float heading)
    {
        this.map = map;
        this.position = position;
        this.heading = heading;
        createPlaneMarker();
        setDirectionLine();
    }

    private void createPlaneMarker()
    {
        plane = map.addMarker(new MarkerOptions()
                .position(position)
                .title("Plane Position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blackaircrafticonsmall))
                .rotation(heading)
                .anchor(0.5f, 0.5f)
                .flat(true));
    }


    private String TAG = "GooglemapsTest";
    private GoogleMap map;
    private Marker plane;
    private LatLng position;
    private Float heading;

    public void setPosition(LatLng position)
    {
        this.position = position;
        plane.setPosition(this.position);
    }

    public void setRotation(float heading)
    {
        this.heading = heading;
        plane.setRotation(this.heading);
    }

    private Polyline directionLine;
    private void setDirectionLine()
    {
        Log.i(TAG, "Heading: " + heading);

        LatLngBounds b = map.getProjection().getVisibleRegion().latLngBounds;

        PolylineOptions options = new PolylineOptions();
        LatLng newPos = calculatePoint(position, heading.doubleValue(), 100000d);
        options.add(newPos);
        options.zIndex(1100);
        options.color(Color.RED);
        options.width(2);

        options.add(position);

        newPos = calculatePoint(position, heading.doubleValue() + 180d, 100000d);
        options.add(newPos);

        directionLine = map.addPolyline(options);
    }

    public void UpdateDirectionLine()
    {
        if (directionLine != null)
        {
            directionLine.remove();
            setDirectionLine();
        }
    }

    private LatLng calculatePoint(LatLng center, Double angle, Double distance)
    {

        Double latTraveledDeg = (1 / 110.54) * (distance / 1000);
        Double longTraveledDeg = (1 / (111.320 * Math.cos(Math.toRadians(center.latitude)))) * (distance/1000);

        Double lat = center.latitude + (Math.sin(Math.toRadians((360 - angle) + 90)) * latTraveledDeg);
        Double lon = center.longitude + (Math.cos(Math.toRadians((360 - angle) + 90)) * longTraveledDeg);

        return new LatLng(lat, lon);
    }
}

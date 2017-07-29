package nl.robenanita.googlemapstest.markers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import org.ksoap2.HeaderProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 17-9-2015.
 */
public class PlaneMarker {
    public PlaneMarker(GoogleMap map, LatLng position, Float heading, Context context)
    {
        this.map = map;
        this.context = context;
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
    private Context context;

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
    private Polyline directionLineDash;
    private void setDirectionLine()
    {
        Log.i(TAG, "Heading: " + heading);

        PolylineOptions options = new PolylineOptions();
        options.zIndex(1100);
        options.color(Color.BLACK);
        options.width(Helpers.convertDpToPixel(3, context));

        List<PatternItem> dashedPattern = Arrays.asList(new Dash(Helpers.convertDpToPixel(20, context)),
                new Gap(Helpers.convertDpToPixel(20, context)));
        PolylineOptions options2 = new PolylineOptions();
        options2.zIndex(1101);
        options2.color(Color.GREEN);
        options2.pattern(dashedPattern);
        options2.width(Helpers.convertDpToPixel(3, context));

        directionLine = map.addPolyline(options);
        directionLineDash = map.addPolyline(options2);
        setDirectionlinePosition();
    }

    private void setDirectionlinePosition()
    {
        if (directionLine != null)
        {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            points.add(calculatePoint(position, heading.doubleValue(), 100000d));
            points.add(position);
            points.add(calculatePoint(position, heading.doubleValue() + 180d, 100000d));
            directionLineDash.setPoints(points);
            directionLine.setPoints(points);
        }
    }

    public void UpdateDirectionLine()
    {
        if (directionLine != null)
        {
            //directionLine.remove();
            //directionLineDash.remove();
            //setDirectionLine();
            setDirectionlinePosition();
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

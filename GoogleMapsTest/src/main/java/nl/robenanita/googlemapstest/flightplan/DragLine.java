package nl.robenanita.googlemapstest.flightplan;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import nl.robenanita.googlemapstest.Helpers;

/**
 * Created by Rob Verhoef on 21-8-2017.
 */

public class DragLine {
    public DragLine(LatLng start, LatLng mid, LatLng end, GoogleMap map, Context context)
    {
        this.map = map;
        this.context = context;

        createPolyLineOptions();

        topLineOptions.add(start,mid,end);
        bottomLineOptions.add(start,mid,end);

        topLine = map.addPolyline(topLineOptions);
        bottomLine = map.addPolyline(bottomLineOptions);

        setMidPoint(mid);
        setCoarseMarkers();
    }

    private void setCoarseMarkers()
    {
        List<LatLng> points = topLine.getPoints();

        Location l1 = Helpers.getLocation(points.get(0));
        Location l2 = Helpers.getLocation(points.get(1));
        Location l3 = Helpers.getLocation(points.get(2));

        if (topMarker == null)
        {
            topMarker = new CoarseMarker(l1, l2);
            topMarker.setCoarseMarker(map, context, topHalfwayPoint);
            bottomMarker = new CoarseMarker(l2, l3);
            bottomMarker.setCoarseMarker(map, context, bottomHalfwayPoint);
        }
        else
        {
            topMarker.UpdateMarker(l1, l2, context, topHalfwayPoint );
            bottomMarker.UpdateMarker(l2, l3, context, bottomHalfwayPoint);
        }
    }

    private void createPolyLineOptions()
    {
        topLineOptions = new PolylineOptions();
        topLineOptions.color(Color.RED);
        topLineOptions.width(Helpers.convertDpToPixel(7, context));
        topLineOptions.zIndex(1010);

        bottomLineOptions = new PolylineOptions();
        bottomLineOptions.color(Color.BLACK);
        bottomLineOptions.width(Helpers.convertDpToPixel(11, context));
        topLineOptions.zIndex(1009);
    }

    private GoogleMap map;
    private Polyline topLine;
    private Polyline bottomLine;
    private PolylineOptions topLineOptions;
    private PolylineOptions bottomLineOptions;
    private CoarseMarker topMarker;
    private CoarseMarker bottomMarker;
    private Context context;

    private LatLng topHalfwayPoint;
    private LatLng bottomHalfwayPoint;

    public void setMidPoint(LatLng midPoint)
    {
        List<LatLng> points = topLine.getPoints();
        points.set(1, midPoint);

        topHalfwayPoint = Helpers.midPoint(points.get(0),points.get(1));
        bottomHalfwayPoint = Helpers.midPoint(points.get(1),points.get(2));

        topLine.setPoints(points);
        bottomLine.setPoints(points);

        setCoarseMarkers();
    }

    public void removeDragLine()
    {
        topLine.remove();
        bottomLine.remove();
        topMarker.RemoveCoarseMarker();
        bottomMarker.RemoveCoarseMarker();
    }
}

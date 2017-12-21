package nl.robenanita.googlemapstest.MapFragment;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.Route.CoarseMarker;

/**
 * Created by Rob Verhoef on 19-12-2017.
 */

public class DeviationLine {
    public DeviationLine(Context context, GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        this.context = context;
        setLineOption();
    }

    private GoogleMap googleMap;
    private Context context;

    public PolylineOptions line_options;
    public Polyline line;
    public MarkerOptions coarseMarkerOptions;
    public Marker coarseMarker;
    private CoarseMarker coarseMarkerObject;
    private LatLng halfwayPoint;

    public void DrawDeviationLine(Location planeLocation)
    {
        Location mapLocation = Helpers.getLocation(getMapCenterLocation());

        if (planeLocation.distanceTo(mapLocation)>5000)
        {
            if (line == null)
            {
//                line_options.add(new LatLng(planeLocation.getLatitude(), planeLocation.getLongitude()));
//                line_options.add(new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude()));
                List<LatLng> points = new ArrayList<>();
                points.add(new LatLng(planeLocation.getLatitude(), planeLocation.getLongitude()));
                points.add(new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude()));
                line = googleMap.addPolyline(line_options);
                line.setPoints(points);

                coarseMarkerObject = new CoarseMarker(planeLocation, mapLocation);
                coarseMarkerObject.setCoarseMarker(googleMap, context, points.get(1));
            }
            else
            {
                List<LatLng> points = line.getPoints();
                points.set(1, new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude()));
                line.setPoints(points);
                coarseMarkerObject.UpdateMarker(planeLocation, mapLocation, context, points.get(1));
            }
        }
        else
        {
            if (line != null) {
                line.remove();
                coarseMarkerObject.RemoveCoarseMarker();
                line = null;
            }
        }

    }

    private LatLng getMidpoint(List<LatLng> points)
    {
        return Helpers.midPoint(points.get(0), points.get(1));
    }

    private LatLng getMapCenterLocation()
    {
        return googleMap.getCameraPosition().target;
    }

    private void setLineOption()
    {
        line_options = new PolylineOptions();
        line_options.color(Color.BLUE);
        line_options.width(Helpers.convertDpToPixel(7, context));
        line_options.geodesic(true);
        line_options.clickable(true);
        line_options.zIndex(1100);
    }
}

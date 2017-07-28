package nl.robenanita.googlemapstest.flightplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashSet;
import java.util.Set;

import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.LegInfoView;
import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 4-8-2014.
 */
public class Leg {
    public Leg(Waypoint from, Waypoint to, Context context)
    {
        this.toWaypoint = to;
        this.fromWaypoint = from;
        distancesAchived = new HashSet<LegInfoView.Distance>();
        distance = LegInfoView.Distance.larger2000Meters;
        endPlan = false;

        trackoptions = new PolylineOptions();
        LatLng p1 = new LatLng(from.location.getLatitude(), from.location.getLongitude());
        LatLng p2 = new LatLng(to.location.getLatitude(), to.location.getLongitude());
        trackoptions.color(Color.BLUE);
        trackoptions.width(Helpers.convertDpToPixel(7, context));
        trackoptions.add(p1);
        trackoptions.add(p2);
        trackoptions.clickable(true);

        halfwayPoint = Helpers.midPoint(new LatLng(from.location.getLatitude(), from.location.getLongitude()),
                new LatLng(to.location.getLatitude(), to.location.getLongitude()));


    }

    public BitmapDescriptor GetIcon(Context context)
    {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inMutable = true;
        Bitmap courseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_marker_square, op);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Helpers.convertDpToPixel(25f, context));
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Canvas aircourseCanvas = new Canvas(courseBitmap);
        aircourseCanvas.drawText(Integer.toString(Math.round(toWaypoint.compass_heading)),
                Helpers.convertDpToPixel(60f, context), Helpers.convertDpToPixel(60f, context), textPaint);

        return BitmapDescriptorFactory.fromBitmap(courseBitmap);

        //return BitmapDescriptorFactory.fromResource(R.drawable.direction_marker_square);
    }

    public void SetCoarseMarker(GoogleMap map, Context context)
    {
        coarseMarkerOptions = new MarkerOptions();
        coarseMarkerOptions.position(halfwayPoint);
        coarseMarkerOptions.title(Float.toString(toWaypoint.compass_heading));
        coarseMarkerOptions.icon(this.GetIcon(context));
        coarseMarkerOptions.anchor(0.5f, 0.5f);
        coarseMarkerOptions.draggable(true);
        coarseMarkerOptions.rotation(toWaypoint.true_track + 90);
        coarseMarker = map.addMarker(coarseMarkerOptions);
    }

    public void RemoveLegFromMap() {
        if (track != null) {
            track.remove();
            track = null;
        }
        if (coarseMarker != null)
        {
            coarseMarker.remove();
            coarseMarker = null;
        }

    }

    private OnDistanceFromWaypoint onDistanceFromWaypoint = null;
    private Set<LegInfoView.Distance> distancesAchived;
    private Waypoint toWaypoint;
    public Waypoint getToWaypoint()
    {
        return this.toWaypoint;
    }
    private Waypoint fromWaypoint;
    public Waypoint getFromWaypoint()
    {
        return this.fromWaypoint;
    }

    private boolean endPlan;

    private Location currectLocation;
    private float distanceTo;
    private float courseTo;
    private float speed;
    private float deviationFromTrack;

    public PolylineOptions trackoptions;
    public Polyline track;
    public MarkerOptions coarseMarkerOptions;
    public Marker coarseMarker;
    private LatLng halfwayPoint;

    public float getDeviationFromTrack()
    {
        return currectLocation.bearingTo(toWaypoint.location) - fromWaypoint.location.bearingTo(toWaypoint.location);
    }

    private LegInfoView.Distance distance;

    public Integer getBearing()
    {
        Integer b = Math.round(courseTo);
        return (b<0) ? 360+b : b ;
    }

    public Integer getTrueTrack()
    {
        Integer t = Math.round(toWaypoint.true_track);
        return (t<0) ? 360+t : t ;
    }

    public float getDistanceNM()
    {
        return distanceTo / 1852f;
    }

    public int getSecondsToGo() {
        // distanceTo = meters
        // speed = meters/second

        if (speed<25) speed = ((float)toWaypoint.ground_speed * 0.514444444f);
        int seconds = Math.round(distanceTo/speed);

        return seconds;
    }

    public Integer getCourseTo()
    {
        Integer c =  Math.round(this.currectLocation.bearingTo(toWaypoint.location));
        return (c<0) ? 360+c : c;
    }

    public void setCurrectLocation(Location currentLocation)
    {
        this.currectLocation = currentLocation;
        distanceTo = currentLocation.distanceTo(toWaypoint.location);
        courseTo = currentLocation.bearingTo(toWaypoint.location);
        speed = currentLocation.getSpeed();

        //courseTo = (courseTo<0) ? 360+courseTo : courseTo;

        deviationFromTrack = getBearing() - toWaypoint.true_track;

        if (distanceTo>=2000) distance = LegInfoView.Distance.larger2000Meters;
        if (distanceTo<2000) distance = LegInfoView.Distance.smaller2000Meters;
        if (distanceTo<1000) distance = LegInfoView.Distance.smaller1000Meters;
        if (distanceTo<500) distance = LegInfoView.Distance.smaller500Meters;

        if ((distance == LegInfoView.Distance.smaller1000Meters) &&
                ((toWaypoint.waypointType == WaypointType.destinationAirport) || (toWaypoint.waypointType == WaypointType.alternateAirport)))
        {
            endPlan = true;
        }

        if (distance == LegInfoView.Distance.larger2000Meters) {
            distancesAchived.clear();
            if (onDistanceFromWaypoint != null)
                onDistanceFromWaypoint.onMore2000Meters(distancesAchived.add(LegInfoView.Distance.larger2000Meters));
        }
        if (!endPlan) {
            if (distance == LegInfoView.Distance.smaller2000Meters)
                if (onDistanceFromWaypoint != null)
                    onDistanceFromWaypoint.on2000Meters(distancesAchived.add(LegInfoView.Distance.smaller2000Meters));
            if (distance == LegInfoView.Distance.smaller1000Meters)
                if (onDistanceFromWaypoint != null)
                    onDistanceFromWaypoint.on1000Meters(distancesAchived.add(LegInfoView.Distance.smaller1000Meters));
            if (distance == LegInfoView.Distance.smaller500Meters)
                if (onDistanceFromWaypoint != null)
                    onDistanceFromWaypoint.on500Meters(distancesAchived.add(LegInfoView.Distance.smaller500Meters));
        } else
        {
            if (onDistanceFromWaypoint != null)
                onDistanceFromWaypoint.onArrivedDestination(toWaypoint,
                        distancesAchived.add(LegInfoView.Distance.reachedDestination));
        }
    }

    // 100 meter listener
    // 50 meter listener
    // 25 meter listener
    public void setOnDistanceFromWaypoint( final OnDistanceFromWaypoint d) {onDistanceFromWaypoint = d; }
    public interface OnDistanceFromWaypoint {
        public void on2000Meters(boolean firstHit);
        public void on1000Meters(boolean firstHit);
        public void on500Meters(boolean firstHit);
        public void onMore2000Meters(boolean firstHit);
        public void onArrivedDestination(Waypoint waypoint, boolean FirstHit);
    }
}

package nl.robenanita.googlemapstest.flightplan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.Date;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 3-3-14.
 */
public class Waypoint  implements Comparable<Waypoint>, Serializable {
    public Waypoint()
    {
        location = new Location("waypoint");
        fix_id = 0;
        navaid_id = 0;
        airport_id = 0;

        variation = 0f;
        deviation = 0f;
    }

    public Integer id;
    public Integer flightplan_id;
    public Integer order;
    public String name;
    public Integer airport_id;
    public Integer fix_id;
    public Integer navaid_id;
    public Location location;
    public float frequency;
    public Date eto;
    public Date ato;
    public Date reto;
    public Integer time_leg;
    public Integer time_total;
    public float compass_heading;
    public float magnetic_heading;
    public float true_heading;
    public float true_track;
    public float variation;
    public float deviation;
    public Integer distance_leg;
    public Integer distance_total;
    public Integer ground_speed;
    public Integer wind_speed;
    public float wind_direction;
    public WaypointType waypointType;
    public Circle activeCircle;

    public Marker marker;
    public MarkerOptions markerOptions;

    public BitmapDescriptor GetIcon()
    {
        return BitmapDescriptorFactory.fromResource(R.drawable.greendot);
    }

    @Override
    public int compareTo(Waypoint waypoint) {
        Integer s = waypoint.order;
        return this.order - s;
    }

    public String getWaypointInfo()
    {
        String info = "No waypoint information.";

        info = "Waypoint Information................\n" +
                "Name : " + this.name + "\n" +
                "Heading : " + Math.round(this.compass_heading) + "\n" +
                "Distance : " + (this.distance_leg / 1851)  + " NM\n" +
                "\n" +
                "Location..........................\n" +
                "Latitude  : " + Double.toString(this.location.getLatitude()) + "\n" +
                "Longitude : " + Double.toString(this.location.getLongitude());

        return info;
    }

    public void SetwaypointMarker()
    {
        markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
        markerOptions.title(name);
        markerOptions.icon(GetIcon());
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.draggable(true);
    }

    public void RemoveWaypointMarker()
    {
        if (marker != null) {
            marker.remove();
            marker = null;
        }
        if (activeCircle != null) {
            activeCircle.remove();
            activeCircle = null;
        }
    }

    public void SetVariation(Float variation)
    {
        this.variation = variation;
        setVariationDeviation();
    }

    public void SetDeviation(Float deviation)
    {
        this.deviation = deviation;
        setVariationDeviation();
    }

    private void setVariationDeviation()
    {
        magnetic_heading = this.true_heading - variation;
        if (magnetic_heading<=0) magnetic_heading = magnetic_heading + 360f;
        compass_heading = magnetic_heading - deviation;
        if (compass_heading<=0) compass_heading = compass_heading + 360f;
    }
}

package nl.robenanita.googlemapstest.flightplan;

import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

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
    public Integer distance_leg;
    public Integer distance_total;
    public Integer ground_speed;
    public Integer wind_speed;
    public float wind_direction;
    public WaypointType waypointType;
    public Circle activeCircle;

    public Marker marker;

    public BitmapDescriptor GetIcon()
    {
        return BitmapDescriptorFactory.fromResource(R.drawable.waypoint);
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
}

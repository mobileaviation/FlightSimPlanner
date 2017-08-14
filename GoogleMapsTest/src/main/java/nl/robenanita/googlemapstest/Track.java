package nl.robenanita.googlemapstest;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.Leg;
import nl.robenanita.googlemapstest.flightplan.Waypoint;
import nl.robenanita.googlemapstest.flightplan.WaypointType;

/**
 * Created by Rob Verhoef on 12-5-2014.
 */
public class Track {
    private Location fromLocation;
    private Location toLocation;
    private String ident;
    private Polyline track;
    private Polyline track1;

    private PolylineOptions trackoptions;
    private PolylineOptions trackoptions1;

    private Context context;

    public Track(Context context)
    {
        this.context = context;
    }

    public void RemoveTrack()
    {
        if (track != null) track.remove();
        if (track1 != null) track1.remove();
    }

    public void setFromToLocation(LatLng from, LatLng to, String ident)
    {
        this.ident = ident;
        fromLocation = new Location("from");
        fromLocation.setLatitude(from.latitude);
        fromLocation.setLongitude(from.longitude);
        toLocation = new Location("to");
        toLocation.setLatitude(to.latitude);
        toLocation.setLongitude(to.longitude);
    }

    private void createPolylineOptions()
    {
        trackoptions = new PolylineOptions();
        trackoptions.color(Color.RED);
        trackoptions.width(Helpers.convertDpToPixel(7, context));
        trackoptions.add(new LatLng(fromLocation.getLatitude(), fromLocation.getLongitude()));
        trackoptions.add(new LatLng(toLocation.getLatitude(), toLocation.getLongitude()));
        trackoptions.clickable(true);
        trackoptions.zIndex(1000);

        trackoptions1 = new PolylineOptions();
        trackoptions1.color(Color.BLACK);
        trackoptions1.width(Helpers.convertDpToPixel(11, context));
        trackoptions1.add(new LatLng(fromLocation.getLatitude(), fromLocation.getLongitude()));
        trackoptions1.add(new LatLng(toLocation.getLatitude(), toLocation.getLongitude()));
        trackoptions1.clickable(false);
        trackoptions1.zIndex(990);
    }

    public void DrawTrack(GoogleMap map)
    {
        DrawTrack(map, fromLocation);
    }
    public void DrawTrack(GoogleMap map, Location newLoc)
    {
        fromLocation = newLoc;
        RemoveTrack();

        createPolylineOptions();
        track = map.addPolyline(trackoptions);
        track1 = map.addPolyline(trackoptions1);
    }

    public Integer getBearing()
    {
        Integer b = Math.round(fromLocation.bearingTo(toLocation));
        return (b<0) ? 360+b : b ;
    }

    public Integer getDistanceNM()
    {
        return Math.round(fromLocation.distanceTo(toLocation) / 1852f);
    }

    public String getIdent()
    {
        return ident;
    }

    public Leg getLeg(Location currentLocation, Context context)
    {
        Waypoint from = new Waypoint();
        from.location = currentLocation;
        from.name = this.ident;
        from.waypointType = WaypointType.userwaypoint;
        Waypoint to = new Waypoint();
        to.location = this.toLocation;
        to.name = this.ident;
        to.waypointType = WaypointType.userwaypoint;
        to.ground_speed = (int)currentLocation.getSpeed();
        if ((int)to.ground_speed<5) to.ground_speed = 100;

        Leg l = new Leg(from, to, context);
        l.setCurrectLocation(currentLocation);

        return l;
    }

    public Leg getDirecttoLeg(FlightPlan flightPlan, Location fromLocation, Context context)
    {
        FlightPlan tempFlightplan = new FlightPlan(context);
        tempFlightplan.name = "DirectTo Plan";
        tempFlightplan.destination_airport = flightPlan.alternate_airport;
        tempFlightplan.altitude = flightPlan.altitude;
        tempFlightplan.wind_direction = flightPlan.wind_direction;
        tempFlightplan.wind_speed = flightPlan.wind_speed;
        tempFlightplan.indicated_airspeed = flightPlan.indicated_airspeed;

        Waypoint start = new Waypoint();
        start.waypointType = WaypointType.userwaypoint;
        start.name = "Current Location";
        start.order = 1;
        start.location = fromLocation;

        Waypoint destination = new Waypoint();
        destination.waypointType = WaypointType.destinationAirport;
        destination.name = tempFlightplan.destination_airport.name;
        destination.order = 1000;
        Location l = new Location("Alternate");
        l.setLatitude(tempFlightplan.destination_airport.latitude_deg);
        l.setLongitude(tempFlightplan.destination_airport.longitude_deg);
        destination.location = l;

        tempFlightplan.Waypoints.add(start);
        tempFlightplan.Waypoints.add(destination);
        tempFlightplan.UpdateWaypointsData();

        tempFlightplan.startFlightplan(fromLocation);
        return tempFlightplan.getActiveLeg();
    }

}

package nl.robenanita.googlemapstest;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Route.Leg;
import nl.robenanita.googlemapstest.Route.Waypoint;
import nl.robenanita.googlemapstest.Route.WaypointType;

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

    private Leg directToLeg;

    private Context context;

    public Track(Context context)
    {
        this.context = context;
    }

    public void RemoveTrack()
    {
        if (track != null) track.remove();
        if (track1 != null) track1.remove();
        if (directToLeg != null) directToLeg.RemoveLegFromMap();
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
        return directToLeg.getBearing();
    }

    public Integer getDistanceNM()
    {
        return Math.round(directToLeg.getDistanceNM());
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

    private Route createFlightplan(Waypoint start, Waypoint destination)
    {
        Route tempFlightplan = new Route(context);
        tempFlightplan.name = "DirectTo Plan";
        tempFlightplan.altitude = 1000;
        tempFlightplan.wind_direction = 0;
        tempFlightplan.wind_speed = 0;
        tempFlightplan.indicated_airspeed = 0;

        tempFlightplan.Waypoints.add(start);
        tempFlightplan.Waypoints.add(destination);
        tempFlightplan.UpdateWaypointsData();

        return tempFlightplan;
    }

    private Waypoint createWaypoint(String name, Integer order, Location location)
    {
        Waypoint waypoint = new Waypoint();
        waypoint.name = name;
        waypoint.order = order;
        waypoint.location = location;
        return waypoint;
    }

    public Leg getDirecttoLeg(Location fromLocation, Airport toAirport, Context context)
    {
        Waypoint start = createWaypoint("Current Location", 1, fromLocation);
        start.waypointType = WaypointType.userwaypoint;

        Location l = new Location("DirectToAirport");
        l.setLatitude(toAirport.latitude_deg);
        l.setLongitude(toAirport.longitude_deg);
        Waypoint destination = createWaypoint(toAirport.name, 1000, l);
        destination.waypointType = WaypointType.destinationAirport;

        Route tempFlightplan = createFlightplan(start, destination);
        tempFlightplan.destination_airport = toAirport;
        tempFlightplan.startFlightplan(fromLocation);

        directToLeg = tempFlightplan.getActiveLeg();
        return directToLeg;
    }

    public Leg getDirecttoLeg(Location fromLocation, Navaid toNavaid, Context context)
    {
        Waypoint start = createWaypoint("Current Location", 1, fromLocation);
        start.waypointType = WaypointType.userwaypoint;

        Location l = new Location("DirectToAirport");
        l.setLatitude(toNavaid.latitude_deg);
        l.setLongitude(toNavaid.longitude_deg);
        Waypoint destination = createWaypoint(toNavaid.name, 1000, l);
        destination.waypointType = WaypointType.navaid;

        Route tempFlightplan = createFlightplan(start, destination);
        tempFlightplan.startFlightplan(fromLocation);

        directToLeg = tempFlightplan.getActiveLeg();
        return directToLeg;
    }

    public Leg getDirecttoLeg(Location fromLocation, Fix toFix, Context context)
    {
        Waypoint start = createWaypoint("Current Location", 1, fromLocation);
        start.waypointType = WaypointType.userwaypoint;

        Location l = new Location("DirectToAirport");
        l.setLatitude(toFix.latitude_deg);
        l.setLongitude(toFix.longitude_deg);
        Waypoint destination = createWaypoint(toFix.name, 1000, l);
        destination.waypointType = WaypointType.fix;

        Route tempFlightplan = createFlightplan(start, destination);
        tempFlightplan.startFlightplan(fromLocation);

        directToLeg = tempFlightplan.getActiveLeg();
        return directToLeg;
    }

    public Leg getDirecttoLeg(Location fromLocation, Location toLocation, Context context)
    {
        Waypoint start = createWaypoint("Current Location", 1, fromLocation);
        start.waypointType = WaypointType.userwaypoint;

        Waypoint destination = createWaypoint("to Location", 1000, toLocation);
        destination.waypointType = WaypointType.userwaypoint;

        Route tempFlightplan = createFlightplan(start, destination);
        tempFlightplan.startFlightplan(fromLocation);

        directToLeg = tempFlightplan.getActiveLeg();
        return directToLeg;
    }

    public Leg getDirecttoLeg(Route flightPlan, Location fromLocation, Context context)
    {
        Waypoint start = createWaypoint("Current Location", 1, fromLocation);
        start.waypointType = WaypointType.userwaypoint;

        Location l = new Location("Alternate");
        l.setLatitude(flightPlan.alternate_airport.latitude_deg);
        l.setLongitude(flightPlan.alternate_airport.longitude_deg);
        Waypoint destination = createWaypoint(flightPlan.alternate_airport.name, 1000, l);
        destination.waypointType = WaypointType.destinationAirport;

        Route tempFlightplan = createFlightplan(start, destination);
        tempFlightplan.destination_airport = flightPlan.alternate_airport;
        tempFlightplan.altitude = flightPlan.altitude;
        tempFlightplan.wind_direction = flightPlan.wind_direction;
        tempFlightplan.wind_speed = flightPlan.wind_speed;
        tempFlightplan.indicated_airspeed = flightPlan.indicated_airspeed;
        tempFlightplan.startFlightplan(fromLocation);

        directToLeg = tempFlightplan.getActiveLeg();
        return directToLeg;
    }

}

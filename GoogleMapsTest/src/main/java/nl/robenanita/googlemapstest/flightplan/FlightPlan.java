package nl.robenanita.googlemapstest.flightplan;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.LegInfoView;
import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Runway;
import nl.robenanita.googlemapstest.RunwaysList;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.FlightPlanDataSource;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;

//import com.google.maps.android.MarkerManager;

/**
 * Created by Rob Verhoef on 3-3-14.
 */
public class FlightPlan implements Serializable {
    public FlightPlan(Context context) {
        Waypoints = new ArrayList<Waypoint>();
        departure_airport = new Airport();
        destination_airport = new Airport();
        alternate_airport = new Airport();
        Legs = new ArrayList<Leg>();

        legWaypointIndex = 0;
        distance = LegInfoView.Distance.larger2000Meters;
        endPlan = false;
        showOnlyActive = false;

        bufferPolyline = null;

        this.context = context;
    }

    private String TAG = "GooglemapsTest";

    private Context context;
    public Integer id;
    public String name;
    public Airport departure_airport;
    public Airport destination_airport;
    public Airport alternate_airport;
    public Integer altitude;
    public Integer indicated_airspeed;
    public Integer wind_speed;
    public float wind_direction;
    public ArrayList<Waypoint> Waypoints;
    public ArrayList<Leg> Legs;
    public LegInfoView.Distance distance;
    public Date date;
    public boolean endPlan;
    public boolean showOnlyActive;

    private Leg activeLeg;
    private int legWaypointIndex;

    public void LoadFlightplan(Context context, Integer flightPlan_ID, Integer uniqueID)
    {

        // First load the basis of the flightplan
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(context);
        flightPlanDataSource.open();
        flightPlanDataSource.GetFlightplanByID(flightPlan_ID, this);
        flightPlanDataSource.close();

        // Second, load the airports information
        AirportDataSource airportDataSource = new AirportDataSource(context);
        airportDataSource.open(uniqueID);
        this.departure_airport = airportDataSource.GetAirportByID(this.departure_airport.id);
        this.destination_airport = airportDataSource.GetAirportByID(this.destination_airport.id);
        this.alternate_airport = airportDataSource.GetAirportByID(this.alternate_airport.id);
        airportDataSource.close();

        // Load the waypoint for this flightplan
        flightPlanDataSource.open();
        flightPlanDataSource.GetWaypointsByFlightPlan(this);
        flightPlanDataSource.clearTimes(this, true);
        flightPlanDataSource.close();

        // Create the legs of this flightplan
        int c = 0;
        Waypoint w1 = null;
        Waypoint w2 = null;
        for (Waypoint waypoint: this.Waypoints)
        {
            if (c==0)
                // Get the first waypoint
                w1 = waypoint;
            if (c>0)
            {
                // Get the next waypoint
                w2 = waypoint;
                // Create and add the leg
                Leg l = new Leg(w1, w2, context);
                this.Legs.add(l);
                // Set the endwaypoint as the startwaypoint for the next leg
                w1 = waypoint;
            }
            c++;
        }

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        bufferProperty = propertiesDataSource.GetProperty("BUFFER");
        propertiesDataSource.close(true);

        createBuffer();
    }

    public Geometry buffer;
    public Polyline bufferPolyline;
    public Property bufferProperty;

    private void createLegs()
    {
        RemoveFlightplanTrack();
        Legs.clear();
        int c = 0;
        Waypoint w1 = null;
        Waypoint w2 = null;
        for (Waypoint waypoint: this.Waypoints)
        {
            if (c==0)
                // Get the first waypoint
                w1 = waypoint;
            if (c>0)
            {
                // Get the next waypoint
                w2 = waypoint;
                // Create and add the leg
                Leg l = new Leg(w1, w2, context);
                this.Legs.add(l);
                // Set the endwaypoint as the startwaypoint for the next leg
                w1 = waypoint;
            }
            c++;
        }
    }

    public void DrawFlightplan(GoogleMap map)
    {
        for (Leg leg: this.Legs)
        {
            leg.DrawLeg(map);
            leg.SetCoarseMarker(map, context);
        }
    }

    public void RemoveFlightplanTrack()
    {
        for (Leg leg: this.Legs)
        {
            leg.RemoveLegFromMap();
        }
    }

    public void removeOldFlightplanMarkers()
    {
        for (Waypoint w : this.Waypoints)
        {
            w.RemoveWaypointMarker();
        }
    }

    public  HashMap<Marker, Waypoint> waypointMarkerMap;
    public void ShowFlightplanMarkers(GoogleMap map, Context context)
    {
        waypointMarkerMap = new HashMap<Marker, Waypoint>();
        for(Waypoint waypoint : this.Waypoints)
        {
            if ((waypoint.airport_id == 0) && (waypoint.navaid_id == 0) && (waypoint.fix_id == 0))
            {
                waypoint.SetwaypointMarker();
                waypoint.marker = map.addMarker(waypoint.markerOptions);
                waypointMarkerMap.put(waypoint.marker, waypoint);
            }
        }

//        for (Leg leg: Legs)
//        {
//            leg.SetCoarseMarker(map, context);
//        }
    }

    public void CreateBuffer()
    {
        createBuffer();
    }

    private void createBuffer() {
        if (bufferProperty == null)
        {
            bufferProperty = new Property();
            bufferProperty.name = "BUFFER";
            bufferProperty.value1 = "0.3";
            bufferProperty.value2 = "true";
        }
        Coordinate[] coordinates = new Coordinate[this.Waypoints.size()];
        Integer i = 0;
        for (Waypoint w : this.Waypoints)
        {
            coordinates[i] = new Coordinate(w.location.getLongitude(), w.location.getLatitude());
            i++;
        }
        Geometry g = new GeometryFactory().createLineString(coordinates);
        BufferOp bufOp = new BufferOp(g);
        bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
        buffer = bufOp.getResultGeometry(Double.parseDouble(bufferProperty.value1));

        //Geometry e = buffer.getEnvelope();
    }

    public void LoadRunways(GoogleMap map)
    {
        loadRunwaysperAirport(this.departure_airport, map);
        loadRunwaysperAirport(this.destination_airport, map);
        loadRunwaysperAirport(this.alternate_airport, map);
    }

    public void RemoveAllRunwayMarkers()
    {
        removeRunwayMarkers(this.departure_airport);
        removeRunwayMarkers(this.destination_airport);
        removeRunwayMarkers(this.alternate_airport);
    }

    private void removeRunwayMarkers(Airport airport)
    {
        if (airport.runways != null) {
            for (Runway r : airport.runways) {
                if (r.hiMarker!=null) r.hiMarker.remove();
                if (r.lowMarker!=null) r.lowMarker.remove();
            }
        }
    }

    public void DrawBuffer(GoogleMap map)
    {
        Geometry buffer = this.buffer;
        Coordinate[] coordinates = buffer.getCoordinates();
        PolylineOptions o = new PolylineOptions();
        o.color(Color.RED);
        o.width(2);
        o.zIndex(1000);
        for (Coordinate c : coordinates)
        {
            LatLng p = new LatLng(c.y, c.x);
            o.add(p);

        }

        this.bufferPolyline = map.addPolyline(o);
        this.bufferPolyline.setVisible(Boolean.parseBoolean(this.bufferProperty.value2));
    }

    public void RemoveBuffer()
    {
        if (this.bufferPolyline != null)
        {
            this.bufferPolyline.remove();
            this.bufferPolyline = null;
        }
    }

    private void loadRunwaysperAirport(Airport airport, GoogleMap map)
    {
        RunwaysDataSource runwaysDataSource = new RunwaysDataSource(context);
        runwaysDataSource.open();
        airport.runways = runwaysDataSource.loadRunwaysByAirport(airport);
        runwaysDataSource.close();
        loadRunwayMarkers(airport.runways, map);
    }

    private void loadRunwayMarkers(RunwaysList runways, GoogleMap map)
    {
        if (runways != null) {
            for (Runway runway : runways) {
                if (runway.le_latitude_deg > 0) {
                    MarkerOptions m = new MarkerOptions();
                    m.position(new LatLng(runway.le_latitude_deg, runway.le_longitude_deg));
                    m.icon(BitmapDescriptorFactory.fromResource(R.drawable.runwayarrow));
                    m.rotation((float) runway.le_heading_degT);
                    m.title(runway.le_ident);
                    runway.lowMarker = map.addMarker(m);
                }
                if (runway.he_latitude_deg > 0) {
                    MarkerOptions m1 = new MarkerOptions();
                    m1.position(new LatLng(runway.he_latitude_deg, runway.he_longitude_deg));
                    m1.icon(BitmapDescriptorFactory.fromResource(R.drawable.runwayarrow));
                    m1.rotation((float) runway.he_heading_degT);
                    m1.title(runway.he_ident);
                    runway.hiMarker = map.addMarker(m1);
                }
            }
        }
    }

    private OnDistanceFromWaypoint onDistanceFromWaypoint = null;
    private OnNewActiveWaypoint onNewActiveWaypoint = null;

    public boolean getFlightplanActive(){ return (activeLeg != null); }
    public Leg getActiveLeg()
    {
        return activeLeg;
    }
    public int getActivetoWaypointIndex()
    {
        return legWaypointIndex+1;
    }

    public void setActiveLeg(Leg leg)
    {
        activeLeg = leg;
    }

    public void startFlightplan(Location currentLocation)
    {
        legWaypointIndex = 0;
        if (Waypoints.size()>1) {
            activeLeg = new Leg(Waypoints.get(0), Waypoints.get(1), context);
            activeLeg.setCurrectLocation(currentLocation);
            if (onNewActiveWaypoint != null)
            {
                onNewActiveWaypoint.onOldWaypoint(Waypoints.get(0));
                onNewActiveWaypoint.onActiveWaypoint(Waypoints.get(1));
            }
            showOnlyActive = false;
        }
    }

    public void nextLeg(Location currentLocation)
    {
        if (!endPlan) {
            if (legWaypointIndex < Waypoints.size() - 2) legWaypointIndex++;

            activeLeg = new Leg(Waypoints.get(legWaypointIndex), Waypoints.get(legWaypointIndex + 1), context);
            activeLeg.setCurrectLocation(currentLocation);

            if (onNewActiveWaypoint != null) {
                onNewActiveWaypoint.onOldWaypoint(Waypoints.get(legWaypointIndex));
                onNewActiveWaypoint.onActiveWaypoint(Waypoints.get(legWaypointIndex + 1));
            }
        }
    }

    public Leg updateActiveLeg(Location currentLocation)
    {
        if (activeLeg != null) {
            activeLeg.setOnDistanceFromWaypoint(new Leg.OnDistanceFromWaypoint() {
                @Override
                public void onArrivedDestination(Waypoint waypoint, boolean firstHit) {
                    distance = LegInfoView.Distance.smaller2000Meters;
                    endPlan = true;
                    if (onDistanceFromWaypoint != null)
                        onDistanceFromWaypoint.onArrivedDestination(waypoint, firstHit);
                }

                @Override
                public void on2000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.smaller2000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on2000Meters(firstHit);
                    }
                }

                @Override
                public void on1000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.smaller1000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on1000Meters(firstHit);
                    }
                }

                @Override
                public void on500Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.smaller500Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.on500Meters(firstHit);
                    }
                }

                @Override
                public void onMore2000Meters(boolean firstHit) {
                    if (!endPlan) {
                        distance = LegInfoView.Distance.larger2000Meters;
                        if (onDistanceFromWaypoint != null)
                            onDistanceFromWaypoint.onMore2000Meters(firstHit);
                    }
                }




            });

            activeLeg.setCurrectLocation(currentLocation);

        }
        return activeLeg;
    }

    private class Point implements Comparable<Point>
    {
        public int legIndex;
        public double distance;

        @Override
        public int compareTo(Point point) {
            return (int) Math.round((this.distance * 10000) - (point.distance * 10000));
        }
    }

    public void InsertWaypoint2(Waypoint waypoint)
    {
        ArrayList<Point> points = new ArrayList<Point>();

        int legcount = this.Waypoints.size()-1;

        for (int i=0; i<legcount; i++)
        {
            Waypoint from = this.Waypoints.get(i);
            Waypoint to = this.Waypoints.get(i+1);

            float c1 = from.location.bearingTo(to.location);
            float d1 = from.location.distanceTo(to.location);
            float c2 = from.location.bearingTo(waypoint.location);
            float d2 = from.location.distanceTo(waypoint.location);
            float a1 = c1 - c2;
            float a2 = 180f - (Math.abs(a1) + 90f);
            double cosa2 = Math.cos(Math.toRadians(a2));
            double d3 = cosa2 * d2;
            Log.i(TAG, "90degree distance from: " + from.name + " -> " + to.name + " = " + Double.toString(d3));

            Point p = new Point();
            p.distance = d3;
            p.legIndex = i;
            points.add(p);
        }

        Collections.sort(points);
        Log.i(TAG, "leg closed to track: " + Integer.toString(points.get(0).legIndex) + " with distance: " + Double.toString(points.get(0).distance));

        Waypoint p1 = this.Waypoints.get(points.get(0).legIndex);
        Waypoint p2 = this.Waypoints.get(points.get(0).legIndex+1);

        Integer s = Math.abs((p2.order - p1.order) / 2);
        waypoint.order = p1.order + s;

        Waypoints.add(waypoint);

        Collections.sort(Waypoints);
        UpdateWaypointsData();
    }

    public void InsertWaypoint(Waypoint waypoint)
    {
        ArrayList<Point> points = new ArrayList<Point>();

        int legcount = this.Waypoints.size()-1;

        for (int i=0; i<legcount; i++)
        {
            Waypoint from = this.Waypoints.get(i);
            Waypoint to = this.Waypoints.get(i+1);
            LineSegment lineSegment = new LineSegment(
                    new Coordinate(from.location.getLongitude(), from.location.getLatitude()),
                    new Coordinate(to.location.getLongitude(), to.location.getLatitude())
            );
            double distance = lineSegment.distance(new Coordinate(waypoint.location.getLongitude(),
                    waypoint.location.getLatitude()));

            Point p = new Point();
            p.distance = distance;
            p.legIndex = i;
            points.add(p);
        }

        Collections.sort(points);
        Log.i(TAG, "leg closed to track: " + Integer.toString(points.get(0).legIndex) + " with distance: " + Double.toString(points.get(0).distance));

        Waypoint p1 = this.Waypoints.get(points.get(0).legIndex);
        Waypoint p2 = this.Waypoints.get(points.get(0).legIndex+1);

        Integer s = Math.abs((p2.order - p1.order) / 2);
        waypoint.order = p1.order + s;

        Waypoints.add(waypoint);

        Collections.sort(Waypoints);
        UpdateWaypointsData();
    }

    public void UpdateVariation(Float variation)
    {
        for (Waypoint waypoint: Waypoints)
        {
            waypoint.SetVariation(variation);
        }
    }

    public void UpdateWaypointsData()
    {
        Waypoint fromWaypoint = null;
        Integer distanceTotal = 0;
        for (int i = 0; i<Waypoints.size(); i++)
        {
            if (i==0) fromWaypoint = Waypoints.get(i);
            else
            {
                Waypoint nextWaypoint = Waypoints.get(i);
                nextWaypoint.true_track = fromWaypoint.location.bearingTo(nextWaypoint.location);
                if (nextWaypoint.true_track<0) nextWaypoint.true_track = nextWaypoint.true_track + 360f;

                nextWaypoint.distance_leg = Math.round(fromWaypoint.location.distanceTo(nextWaypoint.location));
                distanceTotal = distanceTotal + nextWaypoint.distance_leg;
                nextWaypoint.distance_total = distanceTotal;

                nextWaypoint.wind_direction = this.wind_direction;
                nextWaypoint.wind_speed = this.wind_speed;

                double windrad = Math.toRadians(nextWaypoint.wind_direction);
                double dirrad = Math.toRadians(nextWaypoint.true_track);
                double zijwind = Math.sin(windrad-dirrad) * nextWaypoint.wind_speed;
                double langswind = Math.cos(windrad-dirrad) * nextWaypoint.wind_speed;
                double opstuurhoek = (zijwind/this.indicated_airspeed) * 60;
                double gs = this.indicated_airspeed - langswind;

                Log.i(TAG, "Wind: " + Float.toString(nextWaypoint.wind_direction) + "/" + nextWaypoint.wind_speed.toString() +
                        " TrueTrack: " + Float.toString(nextWaypoint.true_track) +
                        " ZijWind: " + Double.toString(zijwind) +
                        " LangsWind: " + Double.toString(langswind) +
                        " Opstuurhoek: " + Double.toString(opstuurhoek) +
                        " GS: " + Double.toString(gs));

                nextWaypoint.ground_speed = Math.round((float)gs);
                nextWaypoint.true_heading = nextWaypoint.true_track + Math.round(opstuurhoek);
                if (nextWaypoint.true_heading<0) nextWaypoint.true_heading = nextWaypoint.true_heading +360f;

                nextWaypoint.time_leg = Math.round(((float)(nextWaypoint.distance_leg/1852f) / (float)gs) * 60);
                if (fromWaypoint == null)
                    nextWaypoint.time_total = Math.round(((float)(nextWaypoint.distance_total/1852f) / (float)gs) * 60);
                else
                    if (fromWaypoint.time_total == null)
                        nextWaypoint.time_total = Math.round(((float)(nextWaypoint.distance_total/1852f) / (float)gs) * 60);
                    else
                        nextWaypoint.time_total = fromWaypoint.time_total + nextWaypoint.time_leg;

                nextWaypoint.SetDeviation(nextWaypoint.deviation);
                nextWaypoint.SetVariation(nextWaypoint.variation);

                fromWaypoint = nextWaypoint;
            }
        }

        createBuffer();
        createLegs();
    }

    public Waypoint getBeforeWaypoint(Waypoint waypoint)
    {
        int i = Waypoints.indexOf(waypoint);
        if (i>0)
            return Waypoints.get(i-1);
        else
            return null;
    }

    public Waypoint getAfterWaypoint(Waypoint waypoint)
    {
        int i = Waypoints.indexOf(waypoint);
        if (i<Waypoints.size()-1)
            return Waypoints.get(i+1);
        else
            return null;
    }

    public void setOnDistanceFromWaypoint( final OnDistanceFromWaypoint d) {onDistanceFromWaypoint = d; }
    public interface OnDistanceFromWaypoint {
        public void on2000Meters(boolean firstHit);
        public void on1000Meters(boolean firstHit);
        public void on500Meters(boolean firstHit);
        public void onMore2000Meters(boolean firstHit);
        public void onArrivedDestination(Waypoint waypoint, boolean firstHit);
    }

    public void setOnNewActiveWaypoint(final OnNewActiveWaypoint newActiveWaypoint) {onNewActiveWaypoint = newActiveWaypoint;}
    public interface OnNewActiveWaypoint
    {
        public void onOldWaypoint(Waypoint waypoint);
        public void onActiveWaypoint(Waypoint waypoint);
    }
}

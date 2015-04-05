package nl.robenanita.googlemapstest.flightplan;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.LegInfoView;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.FlightPlanDataSource;

//import com.google.maps.android.MarkerManager;

/**
 * Created by Rob Verhoef on 3-3-14.
 */
public class FlightPlan implements Serializable {
    public FlightPlan() {
        Waypoints = new ArrayList<Waypoint>();
        departure_airport = new Airport();
        destination_airport = new Airport();
        alternate_airport = new Airport();
        trackOptions = new PolylineOptions();

        legWaypointIndex = 0;
        distance = LegInfoView.Distance.larger2000Meters;
        endPlan = false;
        showOnlyActive = false;
    }

    private String TAG = "GooglemapsTest";

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
    public PolylineOptions trackOptions;
    public Polyline track;
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
            activeLeg = new Leg(Waypoints.get(0), Waypoints.get(1));
            activeLeg.setCurrectLocation(currentLocation);
            if (onNewActiveWaypoint != null)
            {
                onNewActiveWaypoint.onOldWaypoint(Waypoints.get(0));
                onNewActiveWaypoint.onActiveWaypoint(Waypoints.get(1));
            }
            showOnlyActive = true;
        }
    }

    public void nextLeg(Location currentLocation)
    {
        if (!endPlan) {
            if (legWaypointIndex < Waypoints.size() - 2) legWaypointIndex++;

            activeLeg = new Leg(Waypoints.get(legWaypointIndex), Waypoints.get(legWaypointIndex + 1));
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

    public void InsertWaypoint_old(Waypoint waypoint)
    {
        //InsertWaypoint2(waypoint);

        class PointDistance
        {
            public Float distance;
            public Integer sortorder;
        }
        // maak een lijst met afstanden tot het nieuwe waypoint en de bijbehorende sortorder

        Float maxDis = 0f;
        ArrayList<PointDistance> pdList = new ArrayList<PointDistance>();
        for (Waypoint p : Waypoints)
        {
            PointDistance pp = new PointDistance();
            pp.distance = p.location.distanceTo(waypoint.location);
            if (pp.distance>maxDis) maxDis = pp.distance;
            pp.sortorder = p.order;
            pdList.add(pp);
        }

        // Loop vervolgens door deze waypoints heen en bepaal bij welke twee het nieuwe waypoint
        // het dichts in de buurt ligt.

        // Zoek het eerste punt
        Float dis = maxDis + 1f;
        PointDistance p1 = null;
        for (PointDistance pd : pdList)
        {
            if (pd.distance<dis)
            {
                p1 = pd;
                dis = pd.distance;
            }
        }

        // Zoek het tweede punt
        PointDistance p2 = null;
        dis = maxDis + 1f;
        for (PointDistance pd : pdList)
        {
            if ((pd.distance<dis) && pd!=p1)
            {
                p2 = pd;
                dis = pd.distance;
            }
        }

        Integer s = Math.abs((p1.sortorder - p2.sortorder) / 2);
        if (p1.sortorder>p2.sortorder)
            waypoint.order = p2.sortorder + s;
        else
            waypoint.order = p1.sortorder + s;

        Waypoints.add(waypoint);

        Collections.sort(Waypoints);
        UpdateWaypointsData();

    }

    public void InsertWaypoint(Waypoint waypoint)
    {
        class Point implements Comparable<Point>
        {
            public int legIndex;
            public double distance;

            @Override
            public int compareTo(Point point) {
                return (int) Math.round(this.distance - point.distance);
            }
        }

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

                fromWaypoint = nextWaypoint;
            }
        }
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

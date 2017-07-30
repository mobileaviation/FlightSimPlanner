package nl.robenanita.googlemapstest.markers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.MarkerProperties;
import nl.robenanita.googlemapstest.database.NavaidsDataSource;

/**
 * Created by Rob Verhoef on 30-7-2017.
 */

public class AviationMarkers extends AsyncTask<String, Integer, Void> {
    private GoogleMap map;
    private AirportDataSource airportSource;
    private boolean cancelled = false;
    private Context context;
    private Map<Integer, Airport> airports;
    private Map<Integer, Navaid> navaids;
    private HashMap<Marker, Airport> airportMarkerMap;
    private HashMap<Marker, Navaid> navaidMarkerMap;
    private Integer uniqueID;
    private LatLngBounds curScreen;
    private CameraPosition cameraPosition;
    private MarkerProperties markerProperties;

    private final String TAG = "AviationMarkers";

    public AviationMarkers(Context context, GoogleMap googleMap, Integer uniqueID,
                           Map<Integer, Airport> airports, Map<Integer, Navaid> navaids,
                           HashMap<Marker, Airport> airportMarkerMap, HashMap<Marker, Navaid> navaidMarkerMap,
                           MarkerProperties markerProperties
                           ) {
        super();
        this.context = context;
        this.airports = airports;
        this.navaids = navaids;
        this.map = googleMap;
        this.uniqueID = uniqueID;
        this.airportMarkerMap = airportMarkerMap;
        this.navaidMarkerMap = navaidMarkerMap;
        this.markerProperties = markerProperties;

        curScreen = map.getProjection().getVisibleRegion().latLngBounds;
        cameraPosition = map.getCameraPosition();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancelled = true;
        if (airportSource != null) airportSource.close();
    }

    @Override
    protected Void doInBackground(String... strings) {
        airportSource = new AirportDataSource(context);
        airportSource.open(uniqueID);
        airports = airportSource.getAirportsByCoordinateAndZoomLevel(this.curScreen,
                this.cameraPosition.zoom, airports, markerProperties);
        airportSource.close();

        NavaidsDataSource navaidsDataSource = new NavaidsDataSource(context);
        navaidsDataSource.open(uniqueID);
        navaids = navaidsDataSource.GetNaviadsByCoordinateAndZoomLevel(this.curScreen, this.cameraPosition.zoom, navaids);
        navaidsDataSource.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (!cancelled) {
            PlaceAirportMarkers(this.cameraPosition.zoom, this.curScreen);
            //PlaceAirportMarkersByMapLocationIDs(this.cameraPosition.zoom, map.getProjection().getVisibleRegion().latLngBounds, iDs);
            PlaceNavaidsMarkers(this.cameraPosition.zoom);
            //setInfoWindow();
        }
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private void PlaceAirportMarkers(Float zoom, LatLngBounds latLngBounds)
    {
        Log.i(TAG, "Trying to place airport markers: " + Integer.toString(airports.size()));

        if (airports != null)
        {
            Iterator<Integer> it = airports.keySet().iterator();

            while(it.hasNext())
            {
                Integer key = it.next();
                Airport val = airports.get(key);

                if (latLngBounds.contains(val.getLatLng())) {
                    if (val.marker == null)
                    {
                        setupAirportMarker(val);
                    }

                    switch (val.type) {
                        case large_airport: {
                            val.marker.setVisible((true));
                            break;
                        }
                        case medium_airport: {
                            val.marker.setVisible(zoom >= 7);
                            break;
                        }
                        case small_airport: {
                            val.marker.setVisible(zoom >= 9);
                            break;
                        }
                        case heliport: {
                            val.marker.setVisible(zoom >= 10);
                            break;
                        }
                        case balloonport: {
                            val.marker.setVisible(zoom >= 10);
                            break;
                        }
                        case seaplane_base: {
                            val.marker.setVisible(zoom >= 9);
                            break;
                        }
                        case closed: {
                            val.marker.setVisible(false);
                            break;
                        }
                    }

                    if (!val.marker.isVisible())
                    {
                        val.marker.remove();
                        val.marker = null;
                    }
                }
                else
                {
                    if (val.marker != null) {
                        val.marker.remove();
                        val.marker = null;
                    }
                }
            }
        }
    }

    private void PlaceNavaidsMarkers(Float zoom)
    {
        Log.i(TAG, "Trying to place navaids markers: " + Integer.toString(navaids.size()));

        if (navaids != null)
        {
            Boolean visible = true;
            visible = (zoom>7);

//            if (curVisible!=visible)
//            {
//                curVisible = visible;
//            }

            //Set<Integer> keys = airports.keySet();
            Iterator<Integer> it = navaids.keySet().iterator();

            while(it.hasNext())
            {
                Integer key = it.next();
                Navaid val = navaids.get(key);
                if (val.marker == null)
                {
                    MarkerOptions m = new MarkerOptions();
                    m.position(new LatLng(val.latitude_deg, val.longitude_deg));
                    m.title(val.ident);
                    m.icon(val.GetIcon());
                    m.anchor(0.5f, 0.5f);
                    val.marker = map.addMarker(m);

                    navaidMarkerMap.put(val.marker, val);
                }

                val.marker.setVisible(visible);
            }
        }
    }

    private void setupAirportMarker(Airport airport)
    {
        if (airport.marker == null)
        {
            MarkerOptions m = new MarkerOptions();
            m.position(airport.getLatLng());
            //m.rotation((float) val.heading);
            m.title(airport.ident);
            m.snippet(airport.ident);


            m.icon(airport.GetIcon((float)airport.heading, airport.ident, context));

            m.anchor(0.5f, 0.5f);
            airport.marker = map.addMarker(m);

            airportMarkerMap.put(airport.marker, airport);
        }
    }
}

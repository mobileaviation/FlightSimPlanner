package nl.robenanita.googlemapstest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nl.robenanita.googlemapstest.Instruments.AirspeedView;
import nl.robenanita.googlemapstest.Instruments.AltimeterView;
import nl.robenanita.googlemapstest.Instruments.CompassView;
import nl.robenanita.googlemapstest.Instruments.HorizonView;
import nl.robenanita.googlemapstest.Instruments.TurnCoordinatorView;
import nl.robenanita.googlemapstest.Instruments.VerticalSpeedIndicatorView;
import nl.robenanita.googlemapstest.Settings.SettingsActivity;
import nl.robenanita.googlemapstest.Tracks.LoadTrack;
import nl.robenanita.googlemapstest.Tracks.LoadTrackActivity;
import nl.robenanita.googlemapstest.Weather.WeatherActivity;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.FixesDataSource;
import nl.robenanita.googlemapstest.database.FlightPlanDataSource;
import nl.robenanita.googlemapstest.database.FrequenciesDataSource;
import nl.robenanita.googlemapstest.database.Helpers;
import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;
import nl.robenanita.googlemapstest.database.MarkerProperties;
import nl.robenanita.googlemapstest.database.NavaidsDataSource;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;
import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.FlightPlanActivateActivity;
import nl.robenanita.googlemapstest.flightplan.FlightPlanActivity;
import nl.robenanita.googlemapstest.flightplan.Leg;
import nl.robenanita.googlemapstest.flightplan.Waypoint;
import nl.robenanita.googlemapstest.flightplan.WaypointType;
import nl.robenanita.googlemapstest.search.SearchActivity;
import nl.robenanita.googlemapstest.search.SearchAirportsPopup;


public class NavigationActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private GoogleMap map;
    private Marker plane;

    private MarkerProperties markerProperties;

    private String TAG = "GooglemapsTest";
    private FSUIPCConnection connection;

    private Integer uniqueID;

    private String ServerIPAddress;
    private int ServerPort;

    private Timer testTimer;

    private Button enableTrackingBtn;
    private Boolean trackingEnabled;
    private Boolean connected;

    public enum ConnectionType
    {
        sim, gps
    }
    private ConnectionType connectionType;

    private Button tileSourceBtn;

    private ImageView compassView;
    private ImageView airspeedView;
    private ImageView altimeterView;
    private ImageView vsiView;
    private ImageView turnCoordinatorView;
    private ImageView horizonView;

    private ScaleBar scaleBar;

    public Map<Integer, Airport> airports;
    public Map<Integer, ArrayList<Airport>> airportsLocs;
    public Map<Integer, Navaid> navaids;
    public boolean curVisible = true;
    public Float curZoom = 0f;
    public LatLng curPosition;
    public Location mCurrentLocation;
    public Airport initAirport;
    public Runway initRunway;
    public MapController mapController;

    public HashMap<Marker, Airport> airportMarkerMap;
    public HashMap<Marker, Navaid> navaidMarkerMap;
    public HashMap<Marker, Waypoint> waypointMarkerMap;

    private SearchAirportsPopup searchAirportsPopup;

    private Boolean instrumentsVisible;

    private LocationTracking locationTracking;
    private LoadTrack loadTrack;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "RESUMING Navigation activity");
    }

    private LegInfoView legInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        PropertiesDataSource p = new PropertiesDataSource(this);
//        p.open();
//        boolean noads  = p.checkNoAdvertisements();
//        p.close();
        setContentView(R.layout.activity_navigation_noadds);

        uniqueID = Helpers.generateUniqueId();
        //setUniqueIDtoDatabase();

        Log.i(TAG, "Starting Flightsim mapping tool with ID: " + Integer.toString(uniqueID));

        legInfoView = (LegInfoView) findViewById(R.id.legInfoPanel);
        legInfoView.setVisibility(View.GONE);

        LinearLayout flightplanLayout = (LinearLayout) findViewById(R.id.flightplanLayout);
        flightplanLayout.setVisibility(View.GONE);

        LinearLayout tracksLayout = (LinearLayout) findViewById(R.id.tracksLayout);
        tracksLayout.setVisibility(View.GONE);

        infoPanel = (InfoPanelFragment) getFragmentManager().findFragmentById(R.id.infoPanelFragment);
        //infoPanel.LoadAdd();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map != null) {
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    //Log.i(TAG, "onCameraChange fired: Zoom level: " + cameraPosition.zoom);
                    Boolean redrawMarkers = false;
                    if (Math.abs(cameraPosition.zoom - curZoom) > 1) {
                        curZoom = cameraPosition.zoom;
                        Log.i(TAG, "Zoom Difference > 1: " + cameraPosition.zoom);
                        redrawMarkers = true;
                    }

                    Location curLoc = new Location("curLoc");
                    curLoc.setLongitude(curPosition.longitude);
                    curLoc.setLatitude(curPosition.latitude);
                    Location camLoc = new Location("camLoc");
                    camLoc.setLongitude(cameraPosition.target.longitude);
                    camLoc.setLatitude(cameraPosition.target.latitude);

                    SetupScaleBar();

                    if (camLoc.distanceTo(curLoc) * cameraPosition.zoom > 200000) {
                        Log.i(TAG, "Distance: " + Float.toString(camLoc.distanceTo(curLoc) * cameraPosition.zoom));
                        curPosition = cameraPosition.target;
                        redrawMarkers = true;
                    }

                    if (redrawMarkers) {
                        //CreateMarkers();
                        SetAirportMarkersByZoomAndBoundary();
                    }


                }
            });

            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if (selectedFlightplan != null) {
                        if (!selectedFlightplan.getFlightplanActive()) {
                            Log.i(TAG, "Long click on " + Double.toString(latLng.latitude) + " : " + Double.toString(latLng.longitude));
                            ShowNewWaypointPopup(latLng);
                        } else {
                            Toast.makeText(getApplicationContext(), "You can not make any changes to an active flightplan"
                                    , Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Before adding waypoint, please create and load a flightplan!"
                                , Toast.LENGTH_LONG).show();
                    }
                }
            });

            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Waypoint w = waypointMarkerMap.get(marker);
                    if (w != null) {
                        w.location.setLatitude(marker.getPosition().latitude);
                        w.location.setLongitude(marker.getPosition().longitude);
                        if (selectedFlightplan != null) {
                            removeAllRunwayMarkers(selectedFlightplan);
                            FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
                            flightPlanDataSource.open();
                            flightPlanDataSource.UpdateInsertWaypoints(selectedFlightplan.Waypoints);
                            flightPlanDataSource.close();
                            LoadFlightplan(selectedFlightplan.id);
                        }
                    }
                }
            });


            UiSettings settings = map.getUiSettings();
            settings.setCompassEnabled(true);
            settings.setRotateGesturesEnabled(false);
            settings.setTiltGesturesEnabled(false);
            settings.setScrollGesturesEnabled(true);
            settings.setZoomControlsEnabled(true);
            settings.setZoomGesturesEnabled(true);

            mapController = new MapController(map, this);

            mapController.setBaseMapType(GoogleMap.MAP_TYPE_TERRAIN);

            mapController.setUpTileProvider();
        }

        LoadProperties();

        trackingEnabled = true;
        connected = false;
        tilesource = 3;


        initInstruments();

        airportMarkerMap = new HashMap<Marker, Airport>();
        navaidMarkerMap = new HashMap<Marker, Navaid>();

        SetupScaleBar();

        setupWakeLock();

        ImageButton closeTracksBtn = (ImageButton) findViewById(R.id.closeTracksBtn);
        closeTracksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout tracksLayout = (LinearLayout) findViewById(R.id.tracksLayout);
                tracksLayout.setVisibility(View.GONE);
                if (loadTrack != null) loadTrack.removeTrack();
            }
        });




    }

    private void closeFlightplanShowAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                closeFlightplan();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        builder.setMessage("Are you sure you want to close this FlightPlan?");
        builder.setTitle("Close Flightplan?");

        AlertDialog closePlanDialog = builder.create();
        closePlanDialog.show();
    }

    private void setUniqueIDtoDatabase()
    {
        AirportDataSource airportDataSource = new AirportDataSource(this);
        airportDataSource.open(uniqueID);
        airportDataSource.setProgramID(uniqueID);
        airportDataSource.close();
    }

    private void closeFlightplan()
    {
        LinearLayout flightplanLayout = (LinearLayout) findViewById(R.id.flightplanLayout);
        flightplanLayout.setVisibility(View.GONE);

        if (selectedFlightplan != null)
        {
            removeOldFlightplanTrack();

            removeAllRunwayMarkers(selectedFlightplan);

            legInfoView.setVisibility(View.GONE);

            selectedFlightplan = null;

            if (track != null) track.RemoveTrack();
        }
    }

    private void removeAllRunwayMarkers(FlightPlan flightPlan)
    {
        removeRunwayMarkers(flightPlan.departure_airport);
        removeRunwayMarkers(flightPlan.destination_airport);
        removeRunwayMarkers(flightPlan.alternate_airport);
    }

    private void RunQuery()
    {
        LocationTrackingDataSource locationTrackingDataSource = new LocationTrackingDataSource(this);
        locationTrackingDataSource.open();
        locationTrackingDataSource.runQuery();
        locationTrackingDataSource.close();
    }

    private void initInstruments()
    {
        if (instrumentsVisible) {
            //compassView = (ImageView) findViewById(R.id.compassView);
            //compassBigView = (ImageView) findViewById(R.id.compassBigView);
            setCompass(90);

            //airspeedView = (ImageView) findViewById(R.id.airspeedView);
            //airspeedBigView = (ImageView) findViewById(R.id.airspeedBigView);
            setAirspeed(0);

            //altimeterView = (ImageView) findViewById(R.id.altimeterView);
            //altimeterBigView = (ImageView) findViewById(R.id.altimeterBigView);
            setAltimeter(0);

            //vsiView = (ImageView) findViewById(R.id.vsiView);
            //vsiBigView = (ImageView) findViewById(R.id.vsiBigView);
            setVsi(0);

            //turnCoordinatorView = (ImageView) findViewById(R.id.turnCoordinatorView);
            //turnCoordinatorBigView = (ImageView) findViewById(R.id.turnCoordinatorBigView);
            setTurnCoordinator(0, 0);

            //horizonView = (ImageView) findViewById(R.id.horizonView);
            //horizonBigView = (ImageView) findViewById(R.id.horizonBigView);
            setHorizon(0, 0);
        }
    }

    private void SetupScaleBar()
    {
        ImageView s = (ImageView) findViewById(R.id.scaleBarView);
        scaleBar = new ScaleBar(this);

        LatLngBounds b = map.getProjection().getVisibleRegion().latLngBounds;

        scaleBar.DrawScaleBar(s, true, b);
//        ScaleBar2 s = (ScaleBar2) findViewById(R.id.scaleBarView);
//        s.invalidate();
    }

    private void LoadProperties() {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open();
        propertiesDataSource.FillProperties();
        markerProperties = propertiesDataSource.getMarkersProperties();
        propertiesDataSource.close();

        connectionType = propertiesDataSource.getConnectionType();
        instrumentsVisible = propertiesDataSource.getInstrumentsVisible();
        LinearLayout ins = (LinearLayout) findViewById(R.id.instrumentsLayout);
        if (instrumentsVisible) ins.setVisibility(View.VISIBLE);
        else ins.setVisibility(View.GONE);

        ServerIPAddress = propertiesDataSource.IpAddress.value1;
        ServerPort = Integer.parseInt(propertiesDataSource.IpAddress.value2);

        initAirport = propertiesDataSource.InitAirport;

        LatLng planePos = null;
        float d = 0f;
        if (propertiesDataSource.InitRunway != null)
        {
            if (propertiesDataSource.InitRunway.active.equals("le"))
            {
                planePos = new LatLng(propertiesDataSource.InitRunway.le_latitude_deg,
                        propertiesDataSource.InitRunway.le_longitude_deg);
                d = (float)propertiesDataSource.InitRunway.le_heading_degT;
            }
            else
            {
                planePos = new LatLng(propertiesDataSource.InitRunway.he_latitude_deg,
                        propertiesDataSource.InitRunway.he_longitude_deg);
                d = (float)propertiesDataSource.InitRunway.he_heading_degT;
            }
        }

        curPosition = planePos;
        planePosition = planePos;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(planePos, Float.parseFloat(propertiesDataSource.InitZoom.value1)));

        if (plane == null)
        {
            plane = map.addMarker(new MarkerOptions()
                    .position(planePos)
                    .title("Plane Position")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blackaircrafticonsmall))
                    .rotation(d)
                    .anchor(0.5f, 0.5f)
                    .flat(true));

            Location l = new Location("plane");
            l.setLatitude(planePos.latitude);
            l.setLongitude(planePos.longitude);
            l.setBearing(d);
            l.setSpeed(0f);
            SetInfoPanel(l);
        }
        else
        {
            if (selectedFlightplan == null)
            {
                plane.setPosition(planePos);
                plane.setRotation(d);

                Location l = new Location("plane");
                l.setLatitude(planePos.latitude);
                l.setLongitude(planePos.longitude);
                l.setBearing(d);
                l.setSpeed(0f);
                SetInfoPanel(l);
            }

            SetAirportMarkersByZoomAndBoundary();
        }

    }

    private void setupWakeLock()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    ASyncAirportMarker aSyncAirportMarker;
    private void SetAirportMarkersByZoomAndBoundary()
    {
        if (aSyncAirportMarker == null) {
            aSyncAirportMarker = new ASyncAirportMarker();
            aSyncAirportMarker.curScreen = map.getProjection().getVisibleRegion().latLngBounds;
            aSyncAirportMarker.cameraPosition = map.getCameraPosition();
            aSyncAirportMarker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            aSyncAirportMarker.cancel(true);
            aSyncAirportMarker = new ASyncAirportMarker();
            aSyncAirportMarker.curScreen = map.getProjection().getVisibleRegion().latLngBounds;
            aSyncAirportMarker.cameraPosition = map.getCameraPosition();
            aSyncAirportMarker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class ASyncAirportMarker extends AsyncTask<String, Integer, Void>
    {
        public LatLngBounds curScreen;
        public CameraPosition cameraPosition;
        AirportDataSource airportSource;
        ArrayList<Integer> iDs;
        boolean cancelled = false;

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancelled = true;
            if (airportSource != null) airportSource.close();
        }

        @Override
        protected Void doInBackground(String... strings) {
            airportSource = new AirportDataSource(getBaseContext());
            airportSource.open(uniqueID);
            airports = airportSource.getAirportsByCoordinateAndZoomLevel(this.curScreen,
                    this.cameraPosition.zoom, airports, markerProperties);
            //iDs = airportSource.getMapLocationIDsByBoundary(this.curScreen);
            //airportsLocs = airportSource.getAirportsByMapLocationID(airportsLocs, iDs);
            airportSource.close();

            NavaidsDataSource navaidsDataSource = new NavaidsDataSource(getBaseContext());
            navaidsDataSource.open(uniqueID);
            navaids = navaidsDataSource.GetNaviadsByCoordinateAndZoomLevel(this.curScreen, this.cameraPosition.zoom, navaids);
            navaidsDataSource.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!cancelled) {
                PlaceAirportMarkers(this.cameraPosition.zoom, map.getProjection().getVisibleRegion().latLngBounds);
                //PlaceAirportMarkersByMapLocationIDs(this.cameraPosition.zoom, map.getProjection().getVisibleRegion().latLngBounds, iDs);
                PlaceNavaidsMarkers(this.cameraPosition.zoom);
                setInfoWindow();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroy app");
        if (testTimer!=null) testTimer.cancel();
        if (connection != null) connection.Close();
        if (mLocationClient != null) mLocationClient.disconnect();
    }

    int tilesource;
    private void setMapSource()
    {
//        final int[] tileSources = new int[4];
//        tileSources[0] = GoogleMap.MAP_TYPE_NORMAL;
//        tileSources[1] = GoogleMap.MAP_TYPE_SATELLITE;
//        tileSources[2] = GoogleMap.MAP_TYPE_HYBRID;
//        tileSources[3] = GoogleMap.MAP_TYPE_TERRAIN;
//
//        tilesource++;
//        if (tilesource==4) tilesource = 0;
//        map.setMapType(tileSources[tilesource]);

        LinearLayout layersSetupLayout = (LinearLayout) findViewById(R.id.layersSetupLayout);
        if (layersSetupLayout.getVisibility() == View.GONE) layersSetupLayout.setVisibility(View.VISIBLE);
        else layersSetupLayout.setVisibility(View.GONE);
    }

    private void setLoadFlightplan()
    {
        Intent activateFlightplanIntent = new Intent(NavigationActivity.this, FlightPlanActivateActivity.class);
        activateFlightplanIntent.putExtra("key", 1);
        NavigationActivity.this.startActivityForResult(activateFlightplanIntent, 300);
    }

    public FlightPlan selectedFlightplan;
    private void LoadFlightplan(Integer flightplan_id)
    {
        if (selectedFlightplan != null)
        {
            removeAllRunwayMarkers(selectedFlightplan);
            removeOldFlightplanTrack();
            selectedFlightplan = null;
        }

        if (track != null) track.RemoveTrack();

        selectedFlightplan = new FlightPlan();
        selectedFlightplan.LoadFlightplan(this, flightplan_id, uniqueID);

        Log.i(TAG, "Selected flightplan: " + selectedFlightplan.name);

        selectedFlightplan.UpdateWaypointsData();

        LoadFlightplanRunways();
        ShowFlightplanTrack();
        SetupFlightplanListeners(selectedFlightplan);
        LoadFlightplanGrid();
        PlaceFlightplanAirportMarkers();
    }

    private void SetupFlightplanListeners(FlightPlan flightPlan)
    {
        flightPlan.setOnNewActiveWaypoint(new FlightPlan.OnNewActiveWaypoint() {
            @Override
            public void onOldWaypoint(Waypoint waypoint) {
                if (waypoint.activeCircle != null) {
                    waypoint.activeCircle.remove();
                    waypoint.activeCircle = null;
                }
            }

            @Override
            public void onActiveWaypoint(Waypoint waypoint) {
                if (waypoint.activeCircle != null) {
                    waypoint.activeCircle.remove();
                    waypoint.activeCircle = null;
                }

                LatLng center = new LatLng(waypoint.location.getLatitude(),
                        waypoint.location.getLongitude());


                CircleOptions circleOptions = new CircleOptions();
                circleOptions.strokeColor(Color.RED);
                circleOptions.strokeWidth(5);
                circleOptions.fillColor(Color.argb(50,100,100,100));
                circleOptions.radius(2000d);
                circleOptions.center(center);
                waypoint.activeCircle = map.addCircle(circleOptions);
            }
        });
    }

//    private void ClearFlightplan(FlightPlan flightPlan)
//    {
//        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(this);
//        flightPlanDataSource.open();
//        flightPlanDataSource.clearTimes(flightPlan, true);
//        flightPlanDataSource.close();
//
//    }

    private void LoadFlightplanRunways()
    {
        class ASyncRunways extends AsyncTask<String, Integer, Void> {
            @Override
            protected Void doInBackground(String... strings) {
                RunwaysDataSource runwaysDataSource = new RunwaysDataSource(getBaseContext());
                runwaysDataSource.open();
                RunwaysList departureRunways;
                departureRunways = runwaysDataSource.loadRunwaysByAirport(selectedFlightplan.departure_airport);
                selectedFlightplan.departure_airport.runways = departureRunways;
                RunwaysList destinationRunways;
                destinationRunways = runwaysDataSource.loadRunwaysByAirport(selectedFlightplan.destination_airport);
                selectedFlightplan.destination_airport.runways = destinationRunways;
                RunwaysList alternateRunways;
                alternateRunways = runwaysDataSource.loadRunwaysByAirport(selectedFlightplan.alternate_airport);
                selectedFlightplan.alternate_airport.runways = alternateRunways;
                runwaysDataSource.close();
                return null;
            }

            @Override
            protected void onPreExecute() {
                Log.i(TAG, "Starting Async Runways loading.");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.i(TAG, "Finished Async Runways loading.");
                for (Runway r : selectedFlightplan.departure_airport.runways) LoadRunwayMarkers(r);
                Log.i(TAG, "Found departure Runways: " + selectedFlightplan.departure_airport.runways.size());
                for (Runway r : selectedFlightplan.destination_airport.runways) LoadRunwayMarkers(r);
                Log.i(TAG, "Found destination Runways: " + selectedFlightplan.destination_airport.runways.size());
                for (Runway r : selectedFlightplan.alternate_airport.runways) LoadRunwayMarkers(r);
                Log.i(TAG, "Found alternate Runways: " + selectedFlightplan.alternate_airport.runways.size());
            }

            private void LoadRunwayMarkers(Runway runway)
            {
                if (runway.le_latitude_deg>0)
                {
                    MarkerOptions m = new MarkerOptions();
                    m.position(new LatLng(runway.le_latitude_deg, runway.le_longitude_deg));
                    m.icon(BitmapDescriptorFactory.fromResource(R.drawable.runwayarrow));
                    m.rotation((float) runway.le_heading_degT);
                    m.title(runway.le_ident);
                    runway.lowMarker = map.addMarker(m);
                }
                if (runway.he_latitude_deg>0)
                {
                    MarkerOptions m1 = new MarkerOptions();
                    m1.position(new LatLng(runway.he_latitude_deg, runway.he_longitude_deg));
                    m1.icon(BitmapDescriptorFactory.fromResource(R.drawable.runwayarrow));
                    m1.rotation((float) runway.he_heading_degT);
                    m1.title(runway.he_ident);
                    runway.hiMarker = map.addMarker(m1);
                }
            }
        }

        ASyncRunways aSyncRunways = new ASyncRunways();
        aSyncRunways.execute();
    }

    private void LoadFlightplanGrid()
    {
        LinearLayout flightplanLayout = (LinearLayout) findViewById(R.id.flightplanLayout);
        flightplanLayout.setVisibility(View.VISIBLE);

        FlightplanGrid flightplanGrid = (FlightplanGrid) getFragmentManager().findFragmentById(R.id.flightplanFragment);
        flightplanGrid.setOnFlightplanEvent(new FlightplanGrid.OnFlightplanEvent() {
            @Override
            public void onVariationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                VariationClick(waypoint);
            }

            @Override
            public void onDeviationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                DeviationClick(waypoint);
            }

            @Override
            public void onTakeoffClicked(Waypoint waypoint, FlightPlan flightPlan) {
                ETOClick(waypoint);
            }

            @Override
            public void onAtoClicked(Waypoint waypoint, FlightPlan flightPlan) {
                ATOClick(waypoint);
            }

            @Override
            public void onMoveUpClicked(Waypoint waypoint, FlightPlan flightPlan) {
                moveWaypoint(flightPlan,waypoint, false);
            }

            @Override
            public void onMoveDownClicked(Waypoint waypoint, FlightPlan flightPlan) {
                moveWaypoint(flightPlan,waypoint, true);
            }

            @Override
            public void onDeleteClickedClicked(Waypoint waypoint, FlightPlan flightPlan) {
                deleteWaypoint(waypoint);
            }

            @Override
            public void onClosePlanClicked(FlightPlan flightPlan) {
                closeFlightplanShowAlert();
            }


        });

        flightplanGrid.LoadFlightplanGrid(selectedFlightplan);
    }

    public void VariationClick(Waypoint waypoint)
    {
        Log.i(TAG, "Variation Button clicked: " + waypoint.name);
        int popupWidth = 200;
        int popupHeight = 300;

        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.variationDeviationPopup);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.variation_deviation_popup, viewGroup);

        final VariationDeviationPopup variationDeviationPopup = new VariationDeviationPopup(this, Layout, HeadingError.variation);
        variationDeviationPopup.setContentView(Layout);
        variationDeviationPopup.setWidth(popupWidth);
        variationDeviationPopup.setHeight(popupHeight);
        variationDeviationPopup.setFocusable(true);
        variationDeviationPopup.SetValue(0);

        variationDeviationPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(variationDeviationPopup.result)
                {
                    Integer v = variationDeviationPopup.GetValue();
                    FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
                    flightPlanDataSource.open();
                    flightPlanDataSource.calculateMagneticHeading(v, selectedFlightplan);
                    flightPlanDataSource.close();
                    LoadFlightplanGrid();
                }
            }
        });

        variationDeviationPopup.showAtLocation(Layout, Gravity.CENTER, 0, 0);
    }

    public void deleteWaypoint(final Waypoint waypoint)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Delete waypoint?");
        alertDialogBuilder
                .setMessage("Delete waypoint: " + waypoint.name + ", are you sure??")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
                        flightPlanDataSource.open();
                        flightPlanDataSource.deleteWaypoint(waypoint);
                        flightPlanDataSource.close();
                        if (waypoint.marker != null) waypoint.marker.remove();
                        selectedFlightplan.Waypoints.remove(waypoint);
                        reloadFlightplan();
                        dialog.cancel();

                    }

                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void moveWaypoint(FlightPlan flightPlan, Waypoint waypoint, Boolean down) {
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
        flightPlanDataSource.open();

        if (down) {
            // Move this waypoint one position down, so move the waypoint following this one, one position up
            flightPlanDataSource.MoveWaypointDown(flightPlan, waypoint);
        }
        else
        {
            flightPlanDataSource.MoveWaypointUp(flightPlan, waypoint);
        }

        flightPlanDataSource.close();

        reloadFlightplan();
    }

    private void reloadFlightplan()
    {
        removeOldFlightplanTrack();
        removeAllRunwayMarkers(selectedFlightplan);

        Collections.sort(selectedFlightplan.Waypoints);
        selectedFlightplan.UpdateWaypointsData();

        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(this);

        flightPlanDataSource.open();

        flightPlanDataSource.clearTimes(selectedFlightplan, false);
        flightPlanDataSource.resetCourses(selectedFlightplan, false);
        flightPlanDataSource.UpdateInsertWaypoints(selectedFlightplan.Waypoints);

        flightPlanDataSource.close();

        LoadFlightplanRunways();
        ShowFlightplanTrack();
        SetupFlightplanListeners(selectedFlightplan);
        LoadFlightplanGrid();
    }

    public void DeviationClick(final Waypoint waypoint)
    {
        Log.i(TAG, "Deviation Button clicked: " + waypoint.name);
        int popupWidth = 200;
        int popupHeight = 300;

        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.variationDeviationPopup);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.variation_deviation_popup, viewGroup);

        final VariationDeviationPopup deviationPopup = new VariationDeviationPopup(this, Layout, HeadingError.deviation);
        deviationPopup.setContentView(Layout);
        deviationPopup.setWidth(popupWidth);
        deviationPopup.setHeight(popupHeight);
        deviationPopup.setFocusable(true);
        deviationPopup.SetValue(0);

        deviationPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(deviationPopup.result)
                {
                    Integer v = deviationPopup.GetValue();
                    FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
                    flightPlanDataSource.open();
                    flightPlanDataSource.calculateCompassHeading(v, waypoint, selectedFlightplan);
                    flightPlanDataSource.close();
                    LoadFlightplanGrid();
                }
            }
        });

        deviationPopup.showAtLocation(Layout, Gravity.CENTER, 0, 0);
    }

    public void ETOClick(final Waypoint waypoint)
    {
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
        flightPlanDataSource.open();
        flightPlanDataSource.calculateETO(new Date(), selectedFlightplan);
        flightPlanDataSource.close();

        legInfoView.setVisibility(View.VISIBLE);
        selectedFlightplan.startFlightplan(mCurrentLocation);
        LoadFlightplanGrid();

        legInfoView.setActiveLeg(selectedFlightplan.getActiveLeg(), LegInfoView.Distance.larger2000Meters);
        infoPanel.setActiveLeg(selectedFlightplan.getActiveLeg());

    }

    public void ATOClick(final Waypoint waypoint)
    {
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
        flightPlanDataSource.open();
        flightPlanDataSource.setATOcalculateRETO(waypoint, selectedFlightplan);
        flightPlanDataSource.close();

        selectedFlightplan.nextLeg(mCurrentLocation);
        LoadFlightplanGrid();

        legInfoView.setActiveLeg(selectedFlightplan.getActiveLeg(), LegInfoView.Distance.larger2000Meters);
        infoPanel.setActiveLeg(selectedFlightplan.getActiveLeg());
    }

//    private void LoadWaypointForFlightplan(FlightPlan flightPlan)
//    {
//        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
//        flightPlanDataSource.open();
//        flightPlan = flightPlanDataSource.GetWaypointsByFlightPlan(selectedFlightplan);
//        flightPlanDataSource.close();
//    }

    private void ShowDirectToPopup()
    {
        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.directToPopupLayout);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.directto_popup, viewGroup);

        int popupWidth = 500;
        int popupHeight = 300;
        final DirectToPopup directToPopupPopup = new DirectToPopup(this, Layout,
                (selectedFlightplan == null) ? null : selectedFlightplan.alternate_airport, this);
        directToPopupPopup.setContentView(Layout);
        directToPopupPopup.setWidth(popupWidth);
        directToPopupPopup.setHeight(popupHeight);
        directToPopupPopup.setFocusable(true);

        directToPopupPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (directToPopupPopup.Result)
                {
                    String ident;
                    LatLng Toloc;
                    if (directToPopupPopup.selectedAirport != null)
                    {
                        ident = (directToPopupPopup.selectedAirport.ident != null) ? directToPopupPopup.selectedAirport.ident
                                : directToPopupPopup.selectedAirport.name;
                        Toloc = new LatLng(directToPopupPopup.selectedAirport.latitude_deg,
                                directToPopupPopup.selectedAirport.longitude_deg);
                        setupTrack(planePosition, Toloc, ident);
                    }
                    if (directToPopupPopup.selectedNavaid != null)
                    {
                        ident = (directToPopupPopup.selectedNavaid.ident != null) ? directToPopupPopup.selectedNavaid.ident
                                : directToPopupPopup.selectedNavaid.name;
                        Toloc = new LatLng(directToPopupPopup.selectedNavaid.latitude_deg,
                                directToPopupPopup.selectedNavaid.longitude_deg);
                        setupTrack(planePosition, Toloc, ident);
                    }
                    if (directToPopupPopup.selectedFix != null)
                    {
                        ident = (directToPopupPopup.selectedFix.ident != null) ? directToPopupPopup.selectedFix.ident
                                : directToPopupPopup.selectedFix.name;
                        Toloc = new LatLng(directToPopupPopup.selectedFix.latitude_deg,
                                directToPopupPopup.selectedFix.longitude_deg);
                        setupTrack(planePosition, Toloc, ident);
                    }
                }
            }
        });

        directToPopupPopup.showAtLocation(Layout, Gravity.CENTER_HORIZONTAL, 0, 0 );
    }

    private void ShowFlightplanTrack()
    {
        if (selectedFlightplan != null)
        {
            LatLng point = null;
            Integer i = 0;
            Coordinate[] coordinates = new Coordinate[selectedFlightplan.Waypoints.size()];

            waypointMarkerMap = new HashMap<Marker, Waypoint>();
            for(Waypoint waypoint : selectedFlightplan.Waypoints)
            {
                point = new LatLng(waypoint.location.getLatitude(), waypoint.location.getLongitude());
                selectedFlightplan.trackOptions.color(Color.BLUE);
                selectedFlightplan.trackOptions.width(5);
                selectedFlightplan.trackOptions.add(point);

                if ((waypoint.airport_id == 0) && (waypoint.navaid_id == 0) && (waypoint.fix_id == 0))
                {
                    MarkerOptions m = new MarkerOptions();
                    m.title(waypoint.name);
                    m.position(new LatLng(waypoint.location.getLatitude(), waypoint.location.getLongitude()));
                    m.title(waypoint.name);
                    m.icon(waypoint.GetIcon());
                    m.anchor(0.5f, 0.5f);
                    m.draggable(true);
                    waypoint.marker = map.addMarker(m);
                    waypointMarkerMap.put(waypoint.marker, waypoint);
                }

                coordinates[i] = new Coordinate(point.longitude, point.latitude);
                i++;
            }

            Geometry g = new GeometryFactory().createLineString(coordinates);

            BufferOp bufOp = new BufferOp(g);
            bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
            Geometry buffer = bufOp.getResultGeometry(.3);
            drawBuffer(buffer);

            Geometry e = buffer.getEnvelope();
            drawBuffer(e);

            selectedFlightplan.track = map.addPolyline(selectedFlightplan.trackOptions);
        }
    }

    private void drawBuffer(Geometry buffer)
    {
        Coordinate[] coordinates = buffer.getCoordinates();
        PolylineOptions o = new PolylineOptions();
        o.color(Color.RED);
        o.width(2);
        for (Coordinate c : coordinates)
        {
            LatLng p = new LatLng(c.y, c.x);
            o.add(p);

        }

        map.addPolyline(o);
    }


    private void setCompass(double Heading)
    {
        if (instrumentsVisible) {
//            if (compassSmall == null) compassSmall = new Compass(false, getResources());
//            compassSmall.rotateDrawable((float) Heading, compassView);
            CompassView c = (CompassView) findViewById(R.id.compassView);
            c.setHeading((float) Heading);
        }
    }

    private void setAirspeed(double Speed)
    {
        if (instrumentsVisible)
        {
            //if (airspeedSmall == null) airspeedSmall = new Airspeed(false, getResources());
            // airspeedSmall.rotateDrawable((float) Speed, airspeedView);

            AirspeedView s = (AirspeedView) findViewById(R.id.airspeedView);
            s.setSpeed((float) Speed);
        }
    }

    private void setAltimeter(double height)
    {
        if (instrumentsVisible) {
//            if (altimeterSmall == null) altimeterSmall = new Altimeter(false, getResources());
//            altimeterSmall.rotateDrawable((float)height, altimeterView);
            AltimeterView a = (AltimeterView) findViewById(R.id.altimeterView);
            a.setHeight((float)height);
        }
    }

    private void setVsi(double speed)
    {
        if (instrumentsVisible) {
//            if (vertivalSpeedIndicatorSmall == null) vertivalSpeedIndicatorSmall = new VertivalSpeedIndicator(false, getResources());
//            vertivalSpeedIndicatorSmall.rotateDrawable((float)speed, vsiView);
            VerticalSpeedIndicatorView v = (VerticalSpeedIndicatorView) findViewById(R.id.vsiView);
            v.setSpeed((float)speed);
        }
    }

    private void setTurnCoordinator(double turn, double ball)
    {
        if (instrumentsVisible) {
//            if (turnCoordinatorSmall == null) turnCoordinatorSmall = new TurnCoordinator(false, getResources());
//            turnCoordinatorSmall.rotateDrawable((float)ball, (float)turn, turnCoordinatorView);
            TurnCoordinatorView t = (TurnCoordinatorView)findViewById(R.id.turnCoordinatorView);
            t.setTurnCoordinator((float)turn, (float)ball);
        }
    }

    private void setHorizon(double bank, double pitch)
    {
        if (instrumentsVisible) {
//            if(horizonSmall== null) horizonSmall = new Horizon(false, getResources());
//            horizonSmall.rotateDrawable((float)bank, (float)pitch, horizonView);
            HorizonView h = (HorizonView) findViewById(R.id.horizonView);
            h.setHorizon((float)bank, (float)pitch);
        }
    }

    public LatLng planePosition;
    private void setPlaneMarker(Location position)
    {
        LatLng planePosition = new LatLng(position.getLatitude(), position.getLongitude());
        if (trackingEnabled)
            map.moveCamera(CameraUpdateFactory.newLatLng(planePosition));

        plane.setPosition(planePosition);
        plane.setRotation(position.getBearing());

    }

    private void connectToServer()
    {
        connection = new FSUIPCConnection(ServerIPAddress, ServerPort);
        connection.Connect();
        connection.SetFSUIPCConnectedListener(new FSUIPCConnection.OnFSUIPCAction() {
            @Override
            public void FSUIPCAction(String message, boolean success) {
                //Log.i(TAG, "FSUIPCConnectedListener fired");
                //connectMenuItem.setEnabled(false);
                //openFsuipcMenuItem.setEnabled(true);

                connection.Open();
            }
        });
        connection.SetFSUIPCOpenListener(new FSUIPCConnection.OnFSUIPCAction() {
            @Override
            public void FSUIPCAction(String message, boolean success) {
                //Log.i(TAG, "FSUIPCOpenListener fired");
                //openFsuipcMenuItem.setEnabled(false);
                //startMenuItem.setEnabled(true);

                connectDisconnectMenuItem.setIcon(R.drawable.connected);

                connected = true;
                locationTracking = new LocationTracking(selectedFlightplan, getBaseContext());

                setTestOffsets();

                connection.Process("attitude");
                startTimer();
            }
        });
        connection.SetFSUIPCErrorListener(new FSUIPCConnection.OnFSUIPCAction() {
            @Override
            public void FSUIPCAction(String message, boolean success) {
                Log.i(TAG, "FSUIPC Error: " + message);
                Toast.makeText(getApplicationContext(), "FSUIPC Error: " + message, Toast.LENGTH_LONG).show();
                if (connection != null)
                    if (connection.isConnected())
                        connection.Close();
                if (testTimer != null) testTimer.cancel();

                locationTracking = null;
                connected = false;

                connectDisconnectMenuItem.setIcon(R.drawable.disconnected);
            }
        });
        connection.SetFSUIPCCloseListener(new FSUIPCConnection.OnFSUIPCAction() {
            @Override
            public void FSUIPCAction(String message, boolean success) {
                connected = false;
                locationTracking = null;

                if (mLocationClient != null)
                    if (mLocationClient.isConnected())
                        mLocationClient.disconnect();

                if (testTimer != null) testTimer.cancel();
                connectDisconnectMenuItem.setIcon(R.drawable.disconnected);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //connectMenuItem = menu.findItem(R.id.action_connect);
        //openFsuipcMenuItem = menu.findItem(R.id.action_connectFsuipc);
        //startMenuItem = menu.findItem(R.id.action_start);
        //searchAirportItem = menu.findItem(R.id.action_searchAirport);
        connectDisconnectMenuItem = menu.findItem(R.id.action_connect_disconnect);
        trackEnabledMenuItem = menu.findItem((R.id.action_tracking_active));

        return true;
    }


    //private MenuItem connectMenuItem;
    //private MenuItem openFsuipcMenuItem;
    //private MenuItem startMenuItem;
    //private MenuItem searchAirportItem;
    private MenuItem connectDisconnectMenuItem;
    private MenuItem trackEnabledMenuItem;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.load_track_item:
            {
                ShowLoadTracksActivity();
                return true;
            }
            case R.id.action_settings:
                return true;
            case R.id.action_isnew:
            {
                showIsNewPopup();
                return true;
            }

            case R.id.action_flightplan:
            {
                //ShowCreateFlightPlanPopup(this);

                ShowCreateFlightPlanActivity();
                return true;
            }
            case R.id.action_searchAirport:
            {
                ShowSearchActivity();
                //ShowSearchAirportPopup();
                return true;
            }
            case R.id.action_flightplan_activate:
            {
                setLoadFlightplan();
                return true;
            }

            case R.id.action_maptype:
            {
                setMapSource();
                return true;
            }
            case R.id.action_settings_item:
            {
                showSettingsActivity();
                return true;
            }
            case R.id.action_tracking_active:
            {
                trackingEnabled = !trackingEnabled;
                if (trackingEnabled) trackEnabledMenuItem.setIcon(R.drawable.trackactive);
                else trackEnabledMenuItem.setIcon(R.drawable.trackinactive);
                return true;
            }
            case R.id.action_connect_disconnect:
            {
                if (connected) {
                    switch (connectionType) {
                        case sim :
                        {
                            if (connection != null) {
                                if (testTimer != null) testTimer.cancel();
                                if (connection != null) connection.Close();
                            }
                            break;
                        }
                        case gps :
                        {
                            // Disconnect gps
                            connected = false;
                            locationTracking = null;
                            connectDisconnectMenuItem.setIcon(R.drawable.disconnected);
                            mLocationClient.disconnect();
                            break;
                        }
                    }
                }
                else {
                    switch (connectionType) {
                        case sim: { connectToServer(); break; }
                        case gps: { connectToGps(); break; }
                    }


                }
                return true;
            }
//            case R.id.action_test_item:
//            {
//                Intent startTestIntent = new Intent(NavigationActivity.this, TestActivity.class);
//                NavigationActivity.this.startActivityForResult(startTestIntent, 500);
//                return true;
//            }
            case R.id.action_DirectTo:
            {
                ShowDirectToPopup();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowLoadTracksActivity() {
        Intent startLoadTracksIntent = new Intent(NavigationActivity.this, LoadTrackActivity.class);
        NavigationActivity.this.startActivityForResult(startLoadTracksIntent, 500);
    }

    private LocationClient mLocationClient;
    private void connectToGps() {
        if(servicesConnected())
        {
            if (mLocationClient == null)
            {
                mLocationClient = new LocationClient(
                        this, this, this
                );


            }

            mLocationClient.connect();
        }
    }

    private void showSettingsActivity() {
        Intent startSettingsIntent = new Intent(NavigationActivity.this, SettingsActivity.class);
        NavigationActivity.this.startActivityForResult(startSettingsIntent, 400);
    }

    @Override
    protected void onStart() {
        super.onStart();

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(NavigationActivity.this);
        propertiesDataSource.open();
        Property p = propertiesDataSource.getMapSetup("SHOWISNEW");
        PackageInfo pinfo = null;
        try {
            pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);

            if (p == null) {
                p = new Property();
                p.name = "SHOWISNEW";
                p.value1 = pinfo.versionName;
                p.value2 = Boolean.toString(true);
                propertiesDataSource.InsertProperty(p);
            }
            else
            {
                if (!p.value1.equals(pinfo.versionName))
                {
                    p.value1 = pinfo.versionName;
                    p.value2 = Boolean.toString(true);
                    propertiesDataSource.updateProperty(p);
                }
            }

            propertiesDataSource.close();

            if (Boolean.parseBoolean(p.value2))
            {

                new Handler().postDelayed(new Runnable() {
                    boolean r = true;
                    public void run() {
                        if (r) showIsNewPopup();
                        r = false;
                    }
                }, 100);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showIsNewPopup()
    {
        int popupWidth = 440;
        int popupHeight = 500;

        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.isNewPopupLayout);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.webisnew_popup, viewGroup);

        final IsNewPopup isNewPopup = new IsNewPopup(this, Layout);

        isNewPopup.setContentView(Layout);
        isNewPopup.setWidth(popupWidth);
        isNewPopup.setHeight(popupHeight);
        isNewPopup.setFocusable(true);

        isNewPopup.showAtLocation(Layout, Gravity.CENTER, 0, 0);

    }

    private void ShowNewWaypointPopup(LatLng Location)
    {
        int popupWidth = 500;
        int popupHeight = 250;

        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.addWaypointPopup);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.add_waypoint, viewGroup);

        final AddWayPointPopup addWayPointPopup = new AddWayPointPopup(this, Layout);

        addWayPointPopup.Location = Location;
        addWayPointPopup.setContentView(Layout);
        addWayPointPopup.setWidth(popupWidth);
        addWayPointPopup.setHeight(popupHeight);
        addWayPointPopup.setFocusable(true);

        addWayPointPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.i(TAG, "AddWayPoint popup dismissed");
                if (addWayPointPopup.Result)
                {
                    removeAllRunwayMarkers(selectedFlightplan);
                    removeOldFlightplanTrack();

                    setupNewWaypointInFlightplan(addWayPointPopup.WaypointName,
                            addWayPointPopup.Location.latitude,
                            addWayPointPopup.Location.longitude,
                            WaypointType.userwaypoint,
                            -1);
                }
                if (addWayPointPopup.Search)
                {
                    Intent searchIntent = new Intent(NavigationActivity.this, SearchActivity.class);
                    searchIntent.putExtra("key", 1);
                    Bundle b = new Bundle();
                    b.putParcelable("location", curPosition);
                    searchIntent.putExtra("location", b);
                    NavigationActivity.this.startActivityForResult(searchIntent, 200);
                }
            }
        });

        addWayPointPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
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

    private void removeOldFlightplanTrack()
    {
        if (selectedFlightplan.track != null)
        {
            selectedFlightplan.trackOptions = new PolylineOptions();
            selectedFlightplan.track.remove();

            for (Waypoint w : selectedFlightplan.Waypoints)
            {
                Marker m = w.marker;
                if (m != null) {
                    m.remove();
                }
                if (w.activeCircle != null) w.activeCircle.remove();
            }
        }
    }

    private void setupNewWaypointInFlightplan(String name, Double latitude, Double longitude, WaypointType type, Integer id)
    {
        Waypoint waypoint = new Waypoint();
        waypoint.name = name;
        waypoint.location.setLatitude(latitude);
        waypoint.location.setLongitude(longitude);
        waypoint.flightplan_id = selectedFlightplan.id;

        if(type==WaypointType.Airport) waypoint.airport_id = id;
        if(type==WaypointType.navaid) waypoint.navaid_id = id;
        if(type==WaypointType.fix) waypoint.fix_id = id;

        selectedFlightplan.InsertWaypoint(waypoint);

        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(getBaseContext());
        flightPlanDataSource.open();
        flightPlanDataSource.UpdateInsertWaypoints(selectedFlightplan.Waypoints);
        flightPlanDataSource.close();

        LoadFlightplanRunways();
        ShowFlightplanTrack();
        LoadFlightplanGrid();
    }

    private void ShowCreateFlightPlanActivity()
    {
        Intent startFlightplanIntent = new Intent(NavigationActivity.this, FlightPlanActivity.class);
        startFlightplanIntent.putExtra("key", 1);
        NavigationActivity.this.startActivityForResult(startFlightplanIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 500)
        {
            if (resultCode == 501)
            {
                // Load the selected track_id
                Integer id = data.getIntExtra("track_id", 0);

                if (id>0) {
                    if (loadTrack != null) loadTrack.removeTrack();
                    loadTrack = new LoadTrack(this, map, id);
                    LinearLayout tracksLayout = (LinearLayout) findViewById(R.id.tracksLayout);
                    tracksLayout.setVisibility(View.VISIBLE);
                    final ListView tracksListView = (ListView) findViewById(R.id.tracksListView);
                    TrackItemAdapter trackItemAdapter = new TrackItemAdapter(loadTrack.getTrackPoints());
                    tracksListView.setAdapter(trackItemAdapter);

                    tracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            tracksListView.setSelection(i);
                            TrackItemAdapter adapter1 = (TrackItemAdapter) adapterView.getAdapter();
                            adapter1.setSelectedIndex(i);
                            adapter1.notifyDataSetChanged();

                            Location selectedLocation;
                            selectedLocation = adapter1.getTrackpoint(i);

                            setPlaneMarker(selectedLocation);
                            SetInfoPanel(selectedLocation);
                        }
                    });
                }
            }
        }

        if (requestCode == CONNECTION_FAILURE_RESOLUTION_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                // Iets doen als de error activity dialog gesloten is..
            }

        }

        if (resultCode==401)
        {
            // Reload properties...
            LoadProperties();
            initInstruments();
        }

        if (resultCode == 301)
        {
            Integer id = data.getIntExtra("id", 0);

            if (requestCode == 300)
            {
                LoadFlightplan(id);

                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });

                builder.setMessage("To activate this flightplan push the 'TakeOff' button!");
                builder.setTitle("Activate flightplan!");

                AlertDialog closePlanDialog = builder.create();
                closePlanDialog.show();
            }
        }

        if (resultCode == 101)
        {
            // Airport selected
            Integer id = data.getIntExtra("id", 0);
            AirportDataSource airportDataSource = new AirportDataSource(getBaseContext());
            airportDataSource.open(uniqueID);
            Airport airport = airportDataSource.GetAirportByID(id);
            airportDataSource.close();
            if (airport != null)
            {
                if (requestCode == 100)
                {
                    LatLng searchPos = new LatLng(airport.latitude_deg, airport.longitude_deg);
                    curPosition = searchPos;
                    map.moveCamera( CameraUpdateFactory.newLatLng(searchPos));
                    setCompassroseMarker(searchPos);
                    SetAirportMarkersByZoomAndBoundary();
                }
                if (requestCode == 200)
                {
                    removeOldFlightplanTrack();
                    setupNewWaypointInFlightplan(airport.name,
                            airport.latitude_deg,
                            airport.longitude_deg,
                            WaypointType.Airport,
                            airport.id);
                }

            }
        }

        if (resultCode == 102)
        {
            // Navaid selected
            Integer id = data.getIntExtra("id", 0);
            NavaidsDataSource navaidsDataSource = new NavaidsDataSource(getBaseContext());
            navaidsDataSource.open(uniqueID);
            Navaid navaid = navaidsDataSource.GetNavaidByID(id);
            navaidsDataSource.close();
            if (navaid != null)
            {
                if (requestCode == 100)
                {
                    LatLng searchPos = new LatLng(navaid.latitude_deg, navaid.longitude_deg);
                    curPosition = searchPos;
                    map.moveCamera( CameraUpdateFactory.newLatLng(searchPos));
                    setCompassroseMarker(searchPos);
                    SetAirportMarkersByZoomAndBoundary();
                }
                if (requestCode == 200)
                {
                    removeOldFlightplanTrack();
                    setupNewWaypointInFlightplan(navaid.name,
                            navaid.latitude_deg,
                            navaid.longitude_deg,
                            WaypointType.navaid,
                            navaid.id);
                }
            }
        }

        if (resultCode == 103)
        {
            // Fix selected
            Integer id = data.getIntExtra("id", 0);
            FixesDataSource fixesDataSource = new FixesDataSource(getBaseContext());
            fixesDataSource.open(uniqueID);
            Fix fix = fixesDataSource.GetFixByID(id);
            fixesDataSource.close();

            if (fix!=null)
            {
                if (requestCode == 100)
                {
                    LatLng searchPos = new LatLng(fix.latitude_deg, fix.longitude_deg);
                    curPosition = searchPos;
                    map.moveCamera( CameraUpdateFactory.newLatLng(searchPos));
                    setCompassroseMarker(searchPos);
                    SetAirportMarkersByZoomAndBoundary();
                }
                if (requestCode == 200)
                {
                    removeOldFlightplanTrack();
                    setupNewWaypointInFlightplan(fix.name,
                            fix.latitude_deg,
                            fix.longitude_deg,
                            WaypointType.fix,
                            fix.id);
                }
            }

        }
    }

    private void ShowSearchActivity()
    {
        Intent startSearchIntent = new Intent(NavigationActivity.this, SearchActivity.class);
        startSearchIntent.putExtra("key", 1);
        Bundle b = new Bundle();
        b.putParcelable("location", curPosition);
        startSearchIntent.putExtra("location", b);
        NavigationActivity.this.startActivityForResult(startSearchIntent, 100);
    }

    private void ShowSearchAirportPopup()
    {
        int popupWidth = 800;
        int popupHeight = 350;

        LinearLayout viewGroup = null;
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.searchairports_popup, viewGroup);

        searchAirportsPopup = new SearchAirportsPopup(this, Layout);

        searchAirportsPopup.setContentView(Layout);
        searchAirportsPopup.setWidth(popupWidth);
        searchAirportsPopup.setHeight(popupHeight);
        searchAirportsPopup.setFocusable(true);

        searchAirportsPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (searchAirportsPopup.Result)
                {
                    Airport a = searchAirportsPopup.SelectedAirport;
                    LatLng planePos = new LatLng(a.latitude_deg, a.longitude_deg);
                    curPosition = planePos;
                    map.moveCamera( CameraUpdateFactory.newLatLng(planePos));
                    SetAirportMarkersByZoomAndBoundary();
                }
            }
        });

        searchAirportsPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
    }

    private void setTestOffsets()
    {
        // Add offset for pitch
        connection.AddOffset(0x0578, "attitude", DataType.Int32);
        // Add offset for bank
        connection.AddOffset(0x057C, "attitude", DataType.Int32);
        // Add offset for Speed
        connection.AddOffset(0x02BC, "attitude", DataType.Int32);
        // Add offset for Latitude
        connection.AddOffset(0x0560, "attitude", DataType.Int64);
        // Add offset for Longitude
        connection.AddOffset(0x0568, "attitude", DataType.Int64);
        // Add offset for Aircrafttype
        connection.AddOffset(0x3D00, "attitude", DataType.String);
        // Add offset for HeadingTrue
        connection.AddOffset(0x0580, "attitude", DataType.Int32);
        // Add offgset for Magnetic variation
        connection.AddOffset(0x02A0, "attitude", DataType.Int32);
        // Add offsets for "Wiskey compass" heading
        connection.AddOffset(0x02CC, "attitude", DataType.Double);
        // Add offsets for Altitude in meters
        connection.AddOffset(0x0574, "attitude", DataType.Int32);
        // Add offset for vertical speed
        connection.AddOffset(0x0842, "attitude", DataType.Int16);
        // Turn coordinator Ball position
        connection.AddOffset(0x036E, "attitude", DataType.Byte);
        // Turn coordinator plane position
        connection.AddOffset(0x037C, "attitude", DataType.Int16);
    }

    private void startTimer()
    {
        testTimer = new Timer();
        testTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                connection.Process("attitude");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String PROVIDER = "simulator";
                            Location loc = new Location(PROVIDER);

                            Object o = connection.ReadOffset(0x02BC);
                            // Airspeed
                            if (o != null)
                            {
                                // calculate knots
                                double as = Double.parseDouble(o.toString()) / 128d;
                                // calculate meters/s
                                as = as * 0.51444444444d;
                                loc.setSpeed((float)as);
                                setAirspeed(as);

                            }

                            // Heading
                            o = connection.ReadOffset(0x02CC);
                            double h = (Double.parseDouble(o.toString()));
                            loc.setBearing((float)h);
                            setCompass(h);


                            // Location
                            o = connection.ReadOffset(0x0560);
                            double lat = 0, lon = 0;
                            if (o != null)
                            {
                                lat = Double.parseDouble(o.toString());
                                lat = lat * (90d/(10001750d*65536d*65536d));
                            }
                            o = connection.ReadOffset(0x0568);
                            if (o != null)
                            {
                                lon = Double.parseDouble(o.toString());
                                lon = lon * (360d / (65536d*65536d*65536d*65536d));
                            }

                            //LatLng curpos = new LatLng(lat, lon);


                            // Height in meters
                            o = connection.ReadOffset(0x0574);
                            double he = 0d;
                            if (o != null) {
                                he = Double.parseDouble(o.toString());
                                he = he * 3.2808399d; // meters to feet
                                setAltimeter(he);
                            }

                            loc.setLatitude(lat);
                            loc.setLongitude(lon);
                            loc.setAltitude(he);
                            loc.setTime((new Date()).getTime());

                            onLocationChanged(loc);

                            o = connection.ReadOffset(0x0842);
                            if (o != null)
                            {
                                double vs = Double.parseDouble(o.toString());
                                vs = (vs * 3.2808399d)/100; // meters to feet
                                setVsi(vs);
                            }

                            o = connection.ReadOffset(0x037C);
                            if (o!=null)
                            {
                                double tcp = Double.parseDouble(o.toString());
                                o = connection.ReadOffset(0x036E);
                                if (o != null)
                                {
                                    double tcb = Double.parseDouble(o.toString());
                                    setTurnCoordinator(tcp, tcb);

                                }
                            }

                            o = connection.ReadOffset(0x057C);
                            if (o != null){
                                double bb = (Double.parseDouble(o.toString()) * 360d) / (65536d*65536d);
                                o = connection.ReadOffset(0x0578);
                                if (o != null){
                                    double pi = (Double.parseDouble(o.toString()) * 360d) / (65536d*65536d);
                                    setHorizon(bb, pi);
                                }
                            }

                        }
                        catch (Exception e) {
                            Log.d(TAG, "Timer Execute exception: " + e.getMessage());
                        }
                    }
                });

            }
        }, 0, 250);
    }

    private InfoPanelFragment infoPanel = null;

    private Track track;
    private void SetInfoPanel(Location location)
    {
        mCurrentLocation = location;
        infoPanel.setLocation(location);

        if (selectedFlightplan != null) {

            selectedFlightplan.setOnDistanceFromWaypoint(new FlightPlan.OnDistanceFromWaypoint() {
                @Override
                public void on2000Meters(boolean firstHit) {
                    if (!selectedFlightplan.endPlan) {
                        if (firstHit)
                            Toast.makeText(NavigationActivity.this, "Les than 2000 Meters from: "
                                    + selectedFlightplan.getActiveLeg().getToWaypoint().name + "\nPREPARE TO TURN!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void on1000Meters(boolean firstHit) {
                    if (!selectedFlightplan.endPlan) {
                        if (firstHit)
                            Toast.makeText(NavigationActivity.this, "Les than 1000 Meters from: "
                                    + selectedFlightplan.getActiveLeg().getToWaypoint().name + "\nSTART TO TURN!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void on500Meters(boolean firstHit) {
                    if (!selectedFlightplan.endPlan) {
                        if (firstHit) {
                            ATOClick(selectedFlightplan.getActiveLeg().getToWaypoint());

                            Toast.makeText(NavigationActivity.this, "Activated next waypoint: "
                                    + selectedFlightplan.getActiveLeg().getToWaypoint().name
                                    + String.format("\nYour next course is : %03d degree", Math.round(selectedFlightplan.getActiveLeg().getToWaypoint().true_track))
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onMore2000Meters(boolean firstHit) {

                }

                @Override
                public void onArrivedDestination(Waypoint waypoint, boolean firstHit) {
                    if (firstHit) {
                        ATOClick(selectedFlightplan.getActiveLeg().getToWaypoint());
                        Toast.makeText(NavigationActivity.this, "You've arrived at your destination: "
                                + selectedFlightplan.getActiveLeg().getToWaypoint().name
                                + "\nThe flightplan is now ended.."
                                , Toast.LENGTH_LONG).show();
                    }
                }
            });



            if (selectedFlightplan.getFlightplanActive()) {
                selectedFlightplan.updateActiveLeg(mCurrentLocation);
                legInfoView.setActiveLeg(selectedFlightplan.getActiveLeg());
                infoPanel.setActiveLeg((track != null) ? track.getDirecttoLeg(selectedFlightplan, mCurrentLocation)
                        : selectedFlightplan.getActiveLeg());
            }
        }
        else
        {
            if (track != null) infoPanel.setActiveLeg(track.getLeg(mCurrentLocation));
        }

    }

    private LatLng oldPoint;
    private float pointDistance = 0;
    private float setTrackPoints(Location newPoint)
    {
        float b = newPoint.getBearing();
        if (oldPoint != null)
        {
            Location loc = new Location("loc old");
            loc.setLatitude(oldPoint.latitude);
            loc.setLongitude(oldPoint.longitude);

            Location locnew = new Location("loc new");
            locnew.setLatitude(newPoint.getLatitude());
            locnew.setLongitude(newPoint.getLongitude());

            float v = loc.distanceTo(locnew);

            if (v>100)
            {
                b = loc.bearingTo(locnew);

                PolylineOptions trackOptions = new PolylineOptions();
                trackOptions.color(Color.GREEN);
                trackOptions.width(5);
                trackOptions.add(oldPoint);
                trackOptions.add(new LatLng(newPoint.getLatitude(), newPoint.getLongitude()));
                oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
                map.addPolyline(trackOptions);

                if (locationTracking != null)
                    locationTracking.SetLocationPoint(newPoint);
            }

            //if (locationTracking != null)
            //    locationTracking.SetLocationPoint(newPoint);
        }
        else
        {
            oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
        }

        return b;
    }

    private void PlaceAirportMarkersByMapLocationIDs(Float zoom, LatLngBounds latLngBounds, ArrayList<Integer> iDs)
    {
        if (airportsLocs != null)
        {
            for (Integer id : iDs)
            {
                ArrayList<Airport> a = airportsLocs.get(id);
                Log.i(TAG, "Trying to place " + Integer.toString(a.size()) + " airport markers for LocationID: " + Integer.toString(id));
                for (Airport val : a)
                {
                    if (val.marker == null)
                    {
                        MarkerOptions m = new MarkerOptions();
                        m.position(new LatLng(val.latitude_deg, val.longitude_deg));
                        //m.rotation((float) val.heading);
                        m.title(val.ident);
                        m.snippet(val.ident);


                        m.icon(val.GetIcon((float)val.heading, val.ident));

                        m.anchor(0.5f, 0.5f);
                        val.marker = map.addMarker(m);

                        airportMarkerMap.put(val.marker, val);
                    }

                    if (latLngBounds.contains(val.marker.getPosition())) {

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
                    }
                }
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


            m.icon(airport.GetIcon((float)airport.heading, airport.ident));

            m.anchor(0.5f, 0.5f);
            airport.marker = map.addMarker(m);

            airportMarkerMap.put(airport.marker, airport);
        }
    }

    private void PlaceFlightplanAirportMarkers()
    {
        if (selectedFlightplan != null)
        {
            Airport dep = airports.get(selectedFlightplan.departure_airport.id);
            if (dep == null) {
                dep = selectedFlightplan.departure_airport;
                airports.put(dep.id, dep);
            }
            setupAirportMarker(dep);
            dep.marker.setVisible(true);

            Airport des = airports.get(selectedFlightplan.destination_airport.id);
            if (des == null) {
                des = selectedFlightplan.destination_airport;
                airports.put(des.id, des);
            }
            setupAirportMarker(des);
            des.marker.setVisible(true);

            Airport alt = airports.get(selectedFlightplan.alternate_airport.id);
            if (alt == null) {
                alt = selectedFlightplan.alternate_airport;
                airports.put(alt.id, alt);
            }
            setupAirportMarker(alt);
            alt.marker.setVisible(true);
        }
    }

    private void PlaceAirportMarkers(Float zoom, LatLngBounds latLngBounds)
    {
        Log.i(TAG, "Trying to place airport markers: " + Integer.toString(airports.size()));

        if (airports != null)
        {
            //Set<Integer> keys = airports.keySet();
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

            PlaceFlightplanAirportMarkers();
        }
    }

    private void PlaceNavaidsMarkers(Float zoom)
    {
        Log.i(TAG, "Trying to place navaids markers: " + Integer.toString(navaids.size()));

        if (navaids != null)
        {
            Boolean visible = true;
            visible = (zoom>7);

            if (curVisible!=visible)
            {
                curVisible = visible;
            }

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

    private void setCompassroseMarker(LatLng location)
    {
        MarkerOptions m = new MarkerOptions();
        m.position(location);
        m.icon(BitmapDescriptorFactory.fromResource(R.drawable.compassrose));
        m.anchor(0.5f, 0.5f);
        map.addMarker(m);
    }

    private void setInfoWindow()
    {
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            private Marker marker;
            private Airport airport;
            private Navaid navaid;
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent startWeatherActivity = new Intent(NavigationActivity.this, WeatherActivity.class);
                airport = airportMarkerMap.get(marker);
                navaid = navaidMarkerMap.get(marker);
                this.marker = marker;

                if (airport !=null) {
                    startWeatherActivity.putExtra("airport_id", (airport == null) ? -1 : airport.id);
                    NavigationActivity.this.startActivityForResult(startWeatherActivity, 500);
                }
            }
        });

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            private View view;
            private Marker marker;
            private Airport airport;
            private Navaid navaid;
            @Override
            public View getInfoWindow(Marker marker) {
                airport = airportMarkerMap.get(marker);
                navaid = navaidMarkerMap.get(marker);
                this.marker = marker;

                if (airport != null)
                {
                    this.view = getLayoutInflater().inflate(
                            R.layout.airport_info_window, null);
                    setUpAirportWindow();
                }

                if (navaid != null)
                {
                    this.view = getLayoutInflater().inflate(
                            R.layout.navaid_info_window, null);
                    setUpNavaidWindow();
                }
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            private void setUpNavaidWindow()
            {
                if (navaid != null)
                {
                    TextView infoTxt = (TextView) view.findViewById(R.id.infoWindowNavaidInfoTxt);
                    String info = navaid.getNavaidInfo();
                    infoTxt.setText(info);
                }
            }

            private void setUpAirportWindow()
            {
                if (airport != null)
                {
                    TextView infoTxt = (TextView) view.findViewById(R.id.infoWindowInfoTxt);
                    TextView runwaysTxt = (TextView) view.findViewById(R.id.infoWindowRunwaysTxt);
                    TextView frequenciesTxt = (TextView) view.findViewById(R.id.infoWindowFrequenciesTxt);

                    RunwaysDataSource runwaysDataSource = new RunwaysDataSource(getBaseContext());
                    runwaysDataSource.open();
                    airport.runways = runwaysDataSource.loadRunwaysByAirport(airport);
                    runwaysDataSource.close();

                    FrequenciesDataSource frequenciesDataSource = new FrequenciesDataSource(getBaseContext());
                    frequenciesDataSource.open();
                    airport.frequencies = frequenciesDataSource.loadFrequenciesByAirport(airport);
                    frequenciesDataSource.close();

                    String info = airport.getAirportInfoString();
                    infoTxt.setText(info);

                    info = airport.getRunwaysInfo();
                    runwaysTxt.setText(info);

                    info = airport.getFrequenciesInfo();
                    frequenciesTxt.setText(info);
                }
            }
        });
    }

    public void setupTrack(LatLng from, LatLng To, String ident)
    {
        if (track != null)
        {
            track.RemoveTrack();
            track = null;
        }

        track = new Track();
        track.setFromToLocation(from, To, ident);


        infoPanel.setTrack(track);
        track.DrawTrack(map);


        Location l = new Location("loc");
        l.setLongitude(planePosition.longitude);
        l.setLatitude(planePosition.latitude);

        if (selectedFlightplan != null) {
            Leg alternateLeg = track.getDirecttoLeg(selectedFlightplan, l);
            selectedFlightplan.setActiveLeg(alternateLeg);
        }

        SetInfoPanel(l);
    }

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(TAG,
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        }
        else
            return false;
    }

    private void showErrorDialog (int errorCode)
    {
        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
            // Create a new DialogFragment for the error dialog
            ErrorDialogFragment errorFragment =
                    new ErrorDialogFragment();
            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(),
                    "Location Updates");
        }
    }

    LocationRequest mLocationRequest;
    private void setupLocationRequest()
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(250);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLocationClient.isConnected()) {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            mCurrentLocation = mLocationClient.getLastLocation();

            locationTracking = new LocationTracking(selectedFlightplan, this);
            connected = true;

            connectDisconnectMenuItem.setIcon(R.drawable.connected);

            setupLocationRequest();

            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
        else
        {
            Toast.makeText(this, "Location Client did not connect!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();

        locationTracking = null;
        connected = false;

        connectDisconnectMenuItem.setIcon(R.drawable.disconnected);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (connected) {
            Log.i(TAG, "Location: " + Double.toString(location.getLatitude()) +
                    " : " + Double.toString(location.getLongitude()));

            setTrackPoints(location);
            setPlaneMarker(location);
            SetInfoPanel(location);
        }
    }
}

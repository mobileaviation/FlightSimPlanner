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
import android.location.LocationListener;
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
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

//import com.appolica.interactiveinfowindow.InfoWindow;
//import com.appolica.interactiveinfowindow.InfoWindowManager;
//import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.common.ConnectionResult;

//import com.google.android.gms.common.GooglePlayServicesClient;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nl.robenanita.googlemapstest.Airspaces.LoadAirspacesAsync;
import nl.robenanita.googlemapstest.Charts.PDFCharts;
import nl.robenanita.googlemapstest.Instruments.AirspeedView;
import nl.robenanita.googlemapstest.Instruments.AltimeterView;
import nl.robenanita.googlemapstest.Instruments.CompassView;
import nl.robenanita.googlemapstest.Instruments.HorizonView;
import nl.robenanita.googlemapstest.Instruments.TurnCoordinatorView;
import nl.robenanita.googlemapstest.Instruments.VerticalSpeedIndicatorView;
import nl.robenanita.googlemapstest.MapFragment.FSPMapFragment;
import nl.robenanita.googlemapstest.MapFragment.TrackingLine;
import nl.robenanita.googlemapstest.Settings.SettingsActivity;
import nl.robenanita.googlemapstest.Tracks.LoadTrack;
import nl.robenanita.googlemapstest.Tracks.LoadTrackActivity;
import nl.robenanita.googlemapstest.Weather.WeatherActivity;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.DBFilesHelper;
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
import nl.robenanita.googlemapstest.markers.PlaneMarker;
import nl.robenanita.googlemapstest.search.SearchActivity;
import nl.robenanita.googlemapstest.search.SearchAirportsPopup;
import nl.robenanita.googlemapstest.Classes.PlanePosition;


public class NavigationActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public GoogleMap map;
    private PlaneMarker plane;

    private MarkerProperties markerProperties;

    private String TAG = "GooglemapsTest";
    private FSUIPCConnection connection;

    private Integer uniqueID;

    private String ServerIPAddress;
    private int ServerPort;

    private LatLng clickedPosition;
    private Boolean routeLineClicked;
    private Leg clickedLeg;

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
    public PlanePosition curPlanePosition;
    public Airport initAirport;
    public Runway initRunway;
    public MapController mapController;

    public HashMap<Marker, Airport> airportMarkerMap;
    public HashMap<Marker, Navaid> navaidMarkerMap;

    private SearchAirportsPopup searchAirportsPopup;

    private Boolean instrumentsVisible;

    private TrackingLine locationTracking;
    private LoadTrack loadTrack;

    private FSPMapFragment fspMapFragment;
//    private InfoWindow waypointInfoWindow;
//    private InfoWindowManager infoWindowManager;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "RESUMING Navigation activity");
    }

    private LegInfoView legInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_noadds);

        uniqueID = Helpers.generateUniqueId();
        //setUniqueIDtoDatabase();

        Log.i(TAG, "Starting Flightsim mapping tool with ID: " + Integer.toString(uniqueID));


        legInfoView = (LegInfoView) findViewById(R.id.legInfoPanel);
        legInfoView.setVisibility(View.GONE);

        final LinearLayout flightplanLayout = (LinearLayout) findViewById(R.id.flightplanLayout);
        flightplanLayout.setVisibility(View.GONE);

        LinearLayout tracksLayout = (LinearLayout) findViewById(R.id.tracksLayout);
        tracksLayout.setVisibility(View.GONE);

        infoPanel = (InfoPanelFragment) getFragmentManager().findFragmentById(R.id.infoPanelFragment);

        routeLineClicked = false;

        fspMapFragment =
        (FSPMapFragment) getFragmentManager().findFragmentById(R.id.FspMap);

        trackingEnabled = true;
        connected = false;

        LoadProperties();

        setupWakeLock();

        ImageButton closeTracksBtn = (ImageButton) findViewById(R.id.closeTracksBtn);
        closeTracksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout tracksLayout = (LinearLayout) findViewById(R.id.tracksLayout);
                tracksLayout.setVisibility(View.GONE);
                fspMapFragment.RemovePreviousTrack();
            }
        });

        fspMapFragment.SetOnMapReadyCallback(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i(TAG, "Found googlemap: " + googleMap.toString());

                fspMapFragment.SetUniqueID(uniqueID);
                fspMapFragment.SetMarkerProperties(markerProperties);
                fspMapFragment.SetMapPosition(curPosition, curZoom);
                fspMapFragment.SetPlaneMarker(curPlanePosition);
                fspMapFragment.SetAviationMarkersByZoomAndBoundary();

                initInstruments();
                SetupScaleBar();

                fspMapFragment.SetOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        SetupScaleBar();
                    }
                });
            }
        });

        fspMapFragment.InitializeMap(NavigationActivity.this);
    }

    public FlightPlan GetSelectedFlightplan()
    {
        return fspMapFragment.GetCurrentFlightplan();
    }

    private void setupAirspaces()
    {
        ArrayList<String> airspacesdbFiles = DBFilesHelper.CopyDatabases(this.getApplicationContext(), true);

        for (String a : airspacesdbFiles)
        {
            LoadAirspacesAsync loadAirspacesAsync = new LoadAirspacesAsync();
            loadAirspacesAsync.context = this;
            loadAirspacesAsync.databaseName = a;
            loadAirspacesAsync.mapView = map;
            loadAirspacesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Log.i(TAG, "Airpace database: " + a + " loaded!");
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to stop navigating?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NavigationActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void setUniqueIDtoDatabase()
    {
        AirportDataSource airportDataSource = new AirportDataSource(this);
        airportDataSource.open(uniqueID);
        airportDataSource.setProgramID(uniqueID);
        airportDataSource.close();
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
            setCompass(90);
            setAirspeed(0);
            setAltimeter(0);
            setVsi(0);
            setTurnCoordinator(0, 0);
            setHorizon(0, 0);
        }
    }

    private void SetupScaleBar()
    {
        ImageView s = (ImageView) findViewById(R.id.scaleBarView);
        scaleBar = new ScaleBar(this);

        LatLngBounds b = fspMapFragment.GetGooglemap().getProjection().getVisibleRegion().latLngBounds;

        scaleBar.DrawScaleBar(s, true, b);
    }

    Property bufferProperty;
    private void LoadProperties() {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open(true);
        propertiesDataSource.FillProperties();
        markerProperties = propertiesDataSource.getMarkersProperties();
        bufferProperty = propertiesDataSource.GetProperty("BUFFER");
        propertiesDataSource.close(true);

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
        if (propertiesDataSource.InitRunway != null) {
            if (propertiesDataSource.InitRunway.active.equals("le")) {
                planePos = new LatLng(propertiesDataSource.InitRunway.le_latitude_deg,
                        propertiesDataSource.InitRunway.le_longitude_deg);
                d = (float) propertiesDataSource.InitRunway.le_heading_degT;
            } else {
                planePos = new LatLng(propertiesDataSource.InitRunway.he_latitude_deg,
                        propertiesDataSource.InitRunway.he_longitude_deg);
                d = (float) propertiesDataSource.InitRunway.he_heading_degT;
            }
        }

        curPosition = planePos;
        curZoom = Float.parseFloat(propertiesDataSource.InitZoom.value1);
//        planePosition = planePos;
        curPlanePosition = new PlanePosition(planePos.latitude, planePos.longitude, 0d, d);
    }


    private void setupWakeLock()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroy app");
        if (testTimer!=null) testTimer.cancel();
        if (connection != null) connection.Close();
        if (mLocationClient != null) mLocationClient.disconnect();
    }

    private void setMapSource()
    {
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



    private void ShowDirectToPopup()
    {
        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.directToPopupLayout);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.directto_popup, viewGroup);

        final DirectToPopup directToPopupPopup = new DirectToPopup(this, Layout,
                (fspMapFragment.GetCurrentFlightplan() == null) ? null : fspMapFragment.GetCurrentFlightplan().alternate_airport, this);
        directToPopupPopup.setContentView(Layout);
        directToPopupPopup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        directToPopupPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
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
//                        setupTrack(planePosition, Toloc, ident);
                    }
                    if (directToPopupPopup.selectedNavaid != null)
                    {
                        ident = (directToPopupPopup.selectedNavaid.ident != null) ? directToPopupPopup.selectedNavaid.ident
                                : directToPopupPopup.selectedNavaid.name;
                        Toloc = new LatLng(directToPopupPopup.selectedNavaid.latitude_deg,
                                directToPopupPopup.selectedNavaid.longitude_deg);
//                        setupTrack(planePosition, Toloc, ident);
                    }
                    if (directToPopupPopup.selectedFix != null)
                    {
                        ident = (directToPopupPopup.selectedFix.ident != null) ? directToPopupPopup.selectedFix.ident
                                : directToPopupPopup.selectedFix.name;
                        Toloc = new LatLng(directToPopupPopup.selectedFix.latitude_deg,
                                directToPopupPopup.selectedFix.longitude_deg);
//                        setupTrack(planePosition, Toloc, ident);
                    }
                }
            }
        });

        directToPopupPopup.showAtLocation(Layout, Gravity.CENTER_HORIZONTAL, 0, 0 );
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
                fspMapFragment.SetupTrackingLine();

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
            case R.id.action_loadaip:
            {
                // Testing polygon create code ****************

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

            case R.id.action_loadchart:
            {
                PDFCharts pdfCharts = new PDFCharts(this);
                pdfCharts.LoadTestPDF();
                return true;
            }
            case R.id.action_DirectTo:
            {
                ShowDirectToPopup();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowLoadTracksActivity() {
        Intent startLoadTracksIntent = new Intent(NavigationActivity.this, LoadTrackActivity.class);
        NavigationActivity.this.startActivityForResult(startLoadTracksIntent, 500);
    }

    private GoogleApiClient mLocationClient;
    private void connectToGps() {
        if(servicesConnected())
        {
            if (mLocationClient == null)
            {
                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();


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
        propertiesDataSource.open(true);
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

            propertiesDataSource.close(true);

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

    private boolean isNewPopupVisible;
    private void showIsNewPopup()
    {
        if (!isNewPopupVisible) {
            int popupWidth = 440;
            int popupHeight = 500;

            LinearLayout viewGroup = (LinearLayout) findViewById(R.id.isNewPopupLayout);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View Layout = layoutInflater.inflate(R.layout.webisnew_popup, viewGroup);

            final IsNewPopup isNewPopup = new IsNewPopup(this, Layout);

            isNewPopup.setContentView(Layout);
            isNewPopup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            isNewPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            isNewPopup.setFocusable(true);

            isNewPopup.showAtLocation(Layout, Gravity.CENTER, 0, 0);
            isNewPopupVisible = true;
        }
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
                    fspMapFragment.LoadPreviousTrack(id);
                    LinearLayout tracksLayout = (LinearLayout) findViewById(R.id.tracksLayout);
                    tracksLayout.setVisibility(View.VISIBLE);
                    final ListView tracksListView = (ListView) findViewById(R.id.tracksListView);
                    TrackItemAdapter trackItemAdapter = new TrackItemAdapter(fspMapFragment.GetPreviousTrackpoints());
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

                            fspMapFragment.SetPlaneMarker(new PlanePosition(selectedLocation.getLatitude(),
                                    selectedLocation.getLongitude(), selectedLocation.getAltitude(),
                                    selectedLocation.getBearing()));
                            if (trackingEnabled) fspMapFragment.SetMapPosition(new LatLng(selectedLocation.getLatitude(),
                                    selectedLocation.getLongitude()));
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
                fspMapFragment.LoadFlightplan(id);
                //LoadFlightplan(id);

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
//                    LatLng searchPos = new LatLng(airport.latitude_deg, airport.longitude_deg);
//                    curPosition = searchPos;
//                    map.moveCamera( CameraUpdateFactory.newLatLng(searchPos));
//                    setCompassroseMarker(searchPos);
//                    SetAirportMarkersByZoomAndBoundary();
                }
                if (requestCode == 200)
                {
//                    selectedFlightplan.removeOldFlightplanMarkers();
//                    setupNewWaypointInFlightplan(airport.name,
//                            airport.latitude_deg,
//                            airport.longitude_deg,
//                            WaypointType.Airport,
//                            airport.id);
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
//                    LatLng searchPos = new LatLng(navaid.latitude_deg, navaid.longitude_deg);
//                    curPosition = searchPos;
//                    map.moveCamera( CameraUpdateFactory.newLatLng(searchPos));
//                    setCompassroseMarker(searchPos);
//                    SetAirportMarkersByZoomAndBoundary();
                }
                if (requestCode == 200)
                {
//                    selectedFlightplan.removeOldFlightplanMarkers();
//                    setupNewWaypointInFlightplan(navaid.name,
//                            navaid.latitude_deg,
//                            navaid.longitude_deg,
//                            WaypointType.navaid,
//                            navaid.id);
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
//                    LatLng searchPos = new LatLng(fix.latitude_deg, fix.longitude_deg);
//                    curPosition = searchPos;
//                    map.moveCamera( CameraUpdateFactory.newLatLng(searchPos));
//                    setCompassroseMarker(searchPos);
//                    SetAirportMarkersByZoomAndBoundary();
                }
                if (requestCode == 200)
                {
//                    selectedFlightplan.removeOldFlightplanMarkers();
//                    setupNewWaypointInFlightplan(fix.name,
//                            fix.latitude_deg,
//                            fix.longitude_deg,
//                            WaypointType.fix,
//                            fix.id);
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

//    private void ShowSearchAirportPopup()
//    {
//        int popupWidth = 800;
//        int popupHeight = 350;
//
//        LinearLayout viewGroup = null;
//        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View Layout = layoutInflater.inflate(R.layout.searchairports_popup, viewGroup);
//
//        searchAirportsPopup = new SearchAirportsPopup(this, Layout);
//
//        searchAirportsPopup.setContentView(Layout);
//        searchAirportsPopup.setWidth(popupWidth);
//        searchAirportsPopup.setHeight(popupHeight);
//        searchAirportsPopup.setFocusable(true);
//
//        searchAirportsPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                if (searchAirportsPopup.Result)
//                {
//                    Airport a = searchAirportsPopup.SelectedAirport;
//                    LatLng planePos = new LatLng(a.latitude_deg, a.longitude_deg);
//                    curPosition = planePos;
//                    map.moveCamera( CameraUpdateFactory.newLatLng(planePos));
//                    SetAirportMarkersByZoomAndBoundary();
//                }
//            }
//        });
//
//        searchAirportsPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
//    }

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
                                //he = he * 3.2808399d; // meters to feet
                                setAltimeter(he * 3.2808399d);
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
        final FlightPlan selectedFlightplan = fspMapFragment.GetCurrentFlightplan();
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
//                            ATOClick(selectedFlightplan.getActiveLeg().getToWaypoint());

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
//                        ATOClick(selectedFlightplan.getActiveLeg().getToWaypoint());
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
                infoPanel.setActiveLeg((track != null) ? track.getDirecttoLeg(selectedFlightplan, mCurrentLocation, this)
                        : selectedFlightplan.getActiveLeg());
            }
        }
        else
        {
            if (track != null) infoPanel.setActiveLeg(track.getLeg(mCurrentLocation, this));
        }

    }

    private void setupAirportMarker(Airport airport)
    {
        if (airport.marker == null)
        {
            MarkerOptions m = new MarkerOptions();
            m.position(airport.getLatLng());
            m.title(airport.ident);
            m.snippet(airport.ident);

            m.icon(airport.GetIcon((float)airport.heading, airport.ident, this));

            m.anchor(0.5f, 0.5f);
            airport.marker = map.addMarker(m);

            airportMarkerMap.put(airport.marker, airport);
        }
    }


//    private void PlaceFlightplanAirportMarkers()
//    {
//        if (selectedFlightplan != null)
//        {
//            Airport dep = airports.get(selectedFlightplan.departure_airport.id);
//            if (dep == null) {
//                dep = selectedFlightplan.departure_airport;
//                //if (dep.runways.size()==0) LoadRunways(dep);
//                airports.put(dep.id, dep);
//            }
//            setupAirportMarker(dep);
//            dep.marker.setVisible(true);
//
//            Airport des = airports.get(selectedFlightplan.destination_airport.id);
//            if (des == null) {
//                des = selectedFlightplan.destination_airport;
//                //if (des.runways.size()==0) LoadRunways(des);
//                airports.put(des.id, des);
//            }
//            setupAirportMarker(des);
//            des.marker.setVisible(true);
//
//            Airport alt = airports.get(selectedFlightplan.alternate_airport.id);
//            if (alt == null) {
//                alt = selectedFlightplan.alternate_airport;
//                //if (alt.runways.size()==0) LoadRunways(alt);
//                airports.put(alt.id, alt);
//            }
//            setupAirportMarker(alt);
//            alt.marker.setVisible(true);
//        }
//    }

    private void setCompassroseMarker(LatLng location)
    {
        MarkerOptions m = new MarkerOptions();
        m.position(location);
        m.icon(BitmapDescriptorFactory.fromResource(R.drawable.compassrose));
        m.anchor(0.5f, 0.5f);
        map.addMarker(m);
    }



//    public void setupTrack(LatLng from, LatLng To, String ident)
//    {
//        if (track != null)
//        {
//            track.RemoveTrack();
//            track = null;
//        }
//
//        track = new Track();
//        track.setFromToLocation(from, To, ident);
//
//
//        infoPanel.setTrack(track);
//        track.DrawTrack(map);
//
//
//        Location l = new Location("loc");
//        l.setLongitude(planePosition.longitude);
//        l.setLatitude(planePosition.latitude);
//
//        if (selectedFlightplan != null) {
//            Leg alternateLeg = track.getDirecttoLeg(selectedFlightplan, l, this);
//            selectedFlightplan.setActiveLeg(alternateLeg);
//        }
//
//        SetInfoPanel(l);
//    }

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
            //mCurrentLocation = mLocationClient.getLastLocation();

            fspMapFragment.SetupTrackingLine();
            connected = true;

            connectDisconnectMenuItem.setIcon(R.drawable.connected);

            setupLocationRequest();

            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    NavigationActivity.this.onLocationChanged(location);
                }
            });

            LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

            //mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
        else
        {
            Toast.makeText(this, "Location Client did not connect!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

//    @Override
//    public void onDisconnected() {
//        Toast.makeText(this, "Disconnected. Please re-connect.",
//                Toast.LENGTH_SHORT).show();
//
//        locationTracking = null;
//        connected = false;
//
//        connectDisconnectMenuItem.setIcon(R.drawable.disconnected);
//    }

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

            fspMapFragment.SetNewTrackingLinePosition(location);
            fspMapFragment.SetPlaneMarker(new PlanePosition(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getBearing()));
            if (trackingEnabled) fspMapFragment.SetMapPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            SetInfoPanel(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

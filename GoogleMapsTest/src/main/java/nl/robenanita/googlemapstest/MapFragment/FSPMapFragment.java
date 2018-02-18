package nl.robenanita.googlemapstest.MapFragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.robenanita.googlemapstest.AddWayPointPopup;
import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Airspaces.Airspace;
import nl.robenanita.googlemapstest.Airspaces.AirspaceInfoFragment;
import nl.robenanita.googlemapstest.Airspaces.Airspaces;
import nl.robenanita.googlemapstest.AnimationHelpers;
import nl.robenanita.googlemapstest.Classes.PlanePosition;
import nl.robenanita.googlemapstest.Fix;
import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.RouteGrid;
import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.InfoPanelFragment;
import nl.robenanita.googlemapstest.InfoWindows.AirportInfoWndFragment;
import nl.robenanita.googlemapstest.InfoWindows.UpdateWaypointListener;
import nl.robenanita.googlemapstest.InfoWindows.NavaidInfoWindowFragment;
import nl.robenanita.googlemapstest.InfoWindows.WaypointFragment;
import nl.robenanita.googlemapstest.LegInfoView;
import nl.robenanita.googlemapstest.MapController;
import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Route.RouteController;
import nl.robenanita.googlemapstest.Settings.LayersSetup.MapStyle;
import nl.robenanita.googlemapstest.Track;
import nl.robenanita.googlemapstest.Tracks.LoadTrack;
import nl.robenanita.googlemapstest.Weather.WeatherActivity;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.RouteDataSource;
import nl.robenanita.googlemapstest.database.FrequenciesDataSource;
import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;
import nl.robenanita.googlemapstest.database.MarkerProperties;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;
import nl.robenanita.googlemapstest.Route.DragLine;
import nl.robenanita.googlemapstest.Route.Leg;
import nl.robenanita.googlemapstest.Route.OnRouteEvent;
import nl.robenanita.googlemapstest.Route.Waypoint;
import nl.robenanita.googlemapstest.Route.WaypointType;
import nl.robenanita.googlemapstest.markers.AviationMarkers;
import nl.robenanita.googlemapstest.markers.PlaneMarker;
import nl.robenanita.googlemapstest.search.SearchPopup;

/**
 * A simple {@link Fragment} subclass.
 */
public class FSPMapFragment extends Fragment {

    private final String TAG = "FSPMapFragment";
    private GoogleMap googleMap;

    private OnMapReadyCallback onMapReadyCallback;
    public void SetOnMapReadyCallback(OnMapReadyCallback onMapReadyCallback)
    { this.onMapReadyCallback = onMapReadyCallback; }

    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    public void SetOnCameraIdleListener(GoogleMap.OnCameraIdleListener onCameraIdleListener)
    { this.onCameraIdleListener = onCameraIdleListener; }

    public Boolean connected;


    private Activity mainActivity;

    private CameraPosition curPosition;
    private PlaneMarker planeMarker;
    private Location curPlaneLocation;

    private MapController mapController;

    private Map<Integer, Airport> airports;
    private Map<Integer, Navaid> navaids;
    private HashMap<Marker, Airport> airportMarkerMap;
    private HashMap<Marker, Navaid> navaidMarkerMap;
    private MarkerProperties markerProperties;
    private Integer uniqueID;

    private Route selectedFlightplan;
    private TrackingLine trackingLine;

    private Leg clickedLeg;

    private LoadTrack loadTrack;

    private RouteController flightplanController;

    private LegInfoView legInfoView;
    private InfoPanelFragment infoPanel;

    private InfoWindow infoWindow;

    private Track track;

    private NewWaypointFragment newWaypointFragment;

    private DeviationLine deviationLine;

    public Boolean airspace_check;

    public FSPMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fspmap, container, false);
        setupAddWaypointLayout();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void InitializeMap(LegInfoView legInfoView, InfoPanelFragment infoPanelFragment)
    {
        this.mainActivity = getActivity();
        this.legInfoView = legInfoView;
        this.infoPanel = infoPanelFragment;
        setupMap();

    }

    private Integer yDelta = 0;
    @SuppressLint("ClickableViewAccessibility")
    private void SetupDrawerListeners() {
        final ImageButton drawerBtn = (ImageButton) getView().findViewById(R.id.fspflightplanhandle);
        drawerBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (FSPMapFragment.this.selectedFlightplan != null) {
                    ImageButton v = (ImageButton) view;
                    LinearLayout l = (LinearLayout) FSPMapFragment.this.getView().findViewById(R.id.fspflightplanLayout);

                    RouteGrid g;
                    g = (RouteGrid) getFragmentManager().findFragmentById(R.id.fspflightplanFragment);
                    if (g==null) g = (RouteGrid) getChildFragmentManager().findFragmentById(R.id.fspflightplanFragment);

                    final int y = (int) motionEvent.getRawY();

                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            LinearLayout.LayoutParams rel_btn = (LinearLayout.LayoutParams) v.getLayoutParams();
                            if (yDelta == 0)
                                yDelta = y - rel_btn.bottomMargin;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) l.getLayoutParams();
                            ll.height = yDelta - y;
                            if (ll.height < 0) ll.height = 0;
                            if (g != null) if (ll.height > g.getHeight(getActivity())) ll.height = g.getHeight(getActivity());
                            l.setLayoutParams(ll);
                            break;
                    }

                    return true;
                }
                return false;

            }
        });

        drawerBtn.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                float y = dragEvent.getY();
                Log.i(TAG, "Dragging drawerBtn to: " + y);
                return true;
            }
        });

    }

    private Boolean flightplanLayoutOpen;
    private void setFlightplanLayout(Boolean open)
    {
        if (open)
        {
            LinearLayout flightplanLayout = (LinearLayout) getView().findViewById(R.id.fspflightplanLayout);
            AnimationHelpers.expand(flightplanLayout, 500, Math.round(Helpers.convertDpToPixel(200, getActivity())));
            flightplanLayoutOpen = true;
        }
        else
        {
            LinearLayout flightplanLayout = (LinearLayout) getView().findViewById(R.id.fspflightplanLayout);
            AnimationHelpers.collapse(flightplanLayout, 500, 0);
            flightplanLayoutOpen = false;
        }
    }

    public GoogleMap GetGooglemap()
    {
        return googleMap;
    }

    public Route GetCurrentFlightplan()
    {
        return selectedFlightplan;
    }

    private void setupAddWaypointLayout()
    {
        newWaypointFragment = (NewWaypointFragment) getFragmentManager().findFragmentById(R.id.newwpFragment);

        newWaypointFragment =
                (NewWaypointFragment) getFragmentManager().findFragmentById(R.id.newwpFragment);
        if (newWaypointFragment == null)
            newWaypointFragment = (NewWaypointFragment) this.getChildFragmentManager().findFragmentById(R.id.newwpFragment);

        setNewWaypointFragmentVisibility(false);
    }

    private void setNewWaypointFragmentVisibility(boolean visibility)
    {
        if (visibility) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.show(newWaypointFragment);
            ft.commit();
        } else
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(newWaypointFragment);
            ft.commit();
        }
    }

    private void setupMap()
    {
        MapFragment tmpFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_fsp_map);
        if (tmpFragment == null)
            tmpFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.fragment_fsp_map);
        final MapFragment mapFragment = tmpFragment;


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                FSPMapFragment.this.googleMap = googleMap;
                googleMap.setMaxZoomPreference(14.5f);
                googleMap.setMinZoomPreference(7f);

                createVariables();
                setUiSettings();
                setMapController();
                setOnCameraMoveListener();
                setOnPolylineClickListeners();
                setOnMarkerDragListeners();
                setOnMarkerClickListeners();
                setOnInfoWindowListeners();
                SetupDrawerListeners();
                setOnMapClickListeners();

                if (onMapReadyCallback != null)
                    FSPMapFragment.this.onMapReadyCallback.onMapReady(googleMap);
            }
        });
    }

    private void createVariables()
    {
        airportMarkerMap = new HashMap<Marker, Airport>();
        navaidMarkerMap = new HashMap<Marker, Navaid>();
    }

    private void setUiSettings()
    {
        UiSettings settings = googleMap.getUiSettings();
        settings.setCompassEnabled(true);
        settings.setRotateGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
    }

    private void setMapController()
    {
        mapController = new MapController(googleMap, mainActivity);
        mapController.setBaseMapType(MapStyle.MAP_TYPE_AVIATION_DAY);
        mapController.setUpTileProvider();
    }

    public MapController GetMapcontroller()
    {
        return mapController;
    }

    public void SetMapPosition(LatLng position, Float zoom)
    {
        curPosition = new CameraPosition(position, zoom,0,0);
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(curPosition));
    }

    public void SetMapPosition(LatLng position)
    {
        if (googleMap != null) {
            float zoom = googleMap.getCameraPosition().zoom;
            curPosition = new CameraPosition(position, zoom, 0, 0);
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(curPosition));
        }
    }

    public void SetPlaneMarker(PlanePosition planePosition)
    {
        curPlaneLocation = new Location("PlaneLocation");
        curPlaneLocation.setLatitude(planePosition.Latitude);
        curPlaneLocation.setLongitude(planePosition.Longitude);
        curPlaneLocation.setBearing((float)planePosition.Heading);
        curPlaneLocation.setAltitude(planePosition.Height);

        if (planeMarker == null)
        {
            planeMarker = new PlaneMarker(googleMap, new LatLng(planePosition.Latitude,
                    planePosition.Longitude), (float)planePosition.Heading, mainActivity);
        }
        else
        {
            planeMarker.setPosition(new LatLng(planePosition.Latitude, planePosition.Longitude));
            planeMarker.setRotation((float)planePosition.Heading);
            planeMarker.UpdateDirectionLine();
        }
    }

    public void SetMarkerProperties(MarkerProperties markerProperties)
    {
        this.markerProperties = markerProperties;
    }

    public void SetUniqueID(Integer uniqueID)
    {
        this.uniqueID = uniqueID;
    }

    private void setOnCameraMoveListener()
    {
        final CameraPosition[] startedPosition = new CameraPosition[1];
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //Log.i(TAG, "Camera Moved to: " + googleMap.getCameraPosition().target.latitude + " : " + googleMap.getCameraPosition().target.longitude);
                curPosition = googleMap.getCameraPosition();
                if (infoWindow != null){
                    infoWindow.MapPositionChanged();
                }

                if (deviationLine == null)
                {
                    deviationLine = new DeviationLine(mainActivity, googleMap);
                }

                deviationLine.DrawDeviationLine(curPlaneLocation);
            }
        });

        googleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                Log.i(TAG, "Moved canceled");
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.i(TAG, "Moved Idle");
                Log.i(TAG, "Camera Moved to: " + googleMap.getCameraPosition().target.latitude + " : " + googleMap.getCameraPosition().target.longitude);
                Log.i(TAG, "Camera Zoomed to: " + googleMap.getCameraPosition().zoom);

                if (newWaypointFragment.isVisible())
                {
                    newWaypointFragment.setNewCameraPosition(googleMap, FSPMapFragment.this.mainActivity);
                }

                Location start = Helpers.getLocation(startedPosition[0].target);
                Location cur = Helpers.getLocation(googleMap.getCameraPosition().target);
                Boolean _do = true;
                Boolean _airspaces_do = airspace_check;
                Boolean _markers_do = true;


                if (connected) {
                    _do = _do && (start.distanceTo(cur)>50);
                    _airspaces_do = _airspaces_do && (start.distanceTo(cur)>500);
                    _markers_do = _markers_do && (start.distanceTo(cur)>500);
                }

                if (_do) {
                    if (_markers_do) SetAviationMarkersByZoomAndBoundary();
                    if (_airspaces_do) ShowAirspacesInfoLayout();
                    if (onCameraIdleListener != null)
                        FSPMapFragment.this.onCameraIdleListener.onCameraIdle();
                }

                Log.i(TAG, "connected: " + connected + " _do: " + _do);
            }
        });

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                Log.i(TAG, "Move Started: " + i);
                startedPosition[0] = googleMap.getCameraPosition();
            }
        });

    }

    private void setOnPolylineClickListeners()
    {
        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.i(TAG, "Polyline clicked " + polyline.toString());
                if (polyline.getTag()=="deviationLine") return;
                LatLng latLng = googleMap.getCameraPosition().target;
                Log.i(TAG, "Clicked Position Lat: " + latLng.latitude + " Lon: " + latLng.longitude);
                setNewWaypointFragmentVisibility(true);
                newWaypointFragment.setNewCameraPosition(googleMap, FSPMapFragment.this.mainActivity);
                newWaypointFragment.SetOnNewWaypointSelectedListener(new NewWapointSelectedListener() {
                    @Override
                    public void NewWaypointSelected(SelectableWaypoint waypoint) {
                        setNewWaypointFragmentVisibility(false);
                        setNewWaypointInFlightplan(waypoint);
                    }
                    @Override
                    public void NewWaypointCanceled()
                    {
                        setNewWaypointFragmentVisibility(false);
                    }
                });
            }
        });
    }

    private void setOnMapClickListeners()
    {
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.i(TAG, "Map Long Click clicked");
                if (selectedFlightplan != null) {
                    Log.i(TAG, "Clicked Position Lat: " + latLng.latitude + " Lon: " + latLng.longitude);
                    setNewWaypointFragmentVisibility(true);
                    newWaypointFragment.setNewCameraPosition(googleMap, FSPMapFragment.this.mainActivity);
                    newWaypointFragment.SetOnNewWaypointSelectedListener(new NewWapointSelectedListener() {
                        @Override
                        public void NewWaypointSelected(SelectableWaypoint waypoint) {
                            setNewWaypointFragmentVisibility(false);
                            setNewWaypointInFlightplan(waypoint);
                        }

                        @Override
                        public void NewWaypointCanceled() {
                            setNewWaypointFragmentVisibility(false);
                        }
                    });
                }
            }
        });
    }

    private void setNewWaypointInFlightplan(SelectableWaypoint waypoint)
    {
        if (selectedFlightplan != null){
            if (!selectedFlightplan.getFlightplanActive())
            {
                selectedFlightplan.RemoveAllRunwayMarkers();
                selectedFlightplan.removeOldFlightplanMarkers();
                selectedFlightplan.RemoveFlightplanTrack();
                //selectedFlightplan.RemoveBuffer();

                setupNewWaypointInFlightplan(waypoint.GetName(),
                        waypoint.GetLatLng().latitude,
                        waypoint.GetLatLng().longitude,
                        WaypointType.userwaypoint,
                        -1);
            }
        }
    }

    private void setOnMarkerDragListeners()
    {
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            //private Polyline dragLine;
            private DragLine dragLine;
            @Override
            public void onMarkerDragStart(Marker marker) {
                Waypoint w = selectedFlightplan.waypointMarkerMap.get(marker);
                Waypoint beforeWaypoint = selectedFlightplan.getBeforeWaypoint(w);
                Waypoint afterWaypoint = selectedFlightplan.getAfterWaypoint(w);
                dragLine = new DragLine(new LatLng(beforeWaypoint.location.getLatitude(), beforeWaypoint.location.getLongitude()),
                        new LatLng(w.location.getLatitude(), w.location.getLongitude()),
                        new LatLng(afterWaypoint.location.getLatitude(), afterWaypoint.location.getLongitude()),
                        googleMap, mainActivity);
            }
            @Override
            public void onMarkerDrag(Marker marker) {
                dragLine.setMidPoint(marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (dragLine != null) {
                    dragLine.removeDragLine();
                    dragLine = null;
                }
                if (selectedFlightplan != null) {
                    Waypoint w = selectedFlightplan.waypointMarkerMap.get(marker);
                    if (w != null) {
                        w.location.setLatitude(marker.getPosition().latitude);
                        w.location.setLongitude(marker.getPosition().longitude);
                        RouteDataSource flightPlanDataSource = new RouteDataSource(mainActivity);
                        flightPlanDataSource.open();
                        flightPlanDataSource.UpdateInsertWaypoints(selectedFlightplan.Waypoints);
                        flightPlanDataSource.close();
                        LoadFlightplan(selectedFlightplan.id);
                    }
                }
            }
        });
    }

    private void setOnMarkerClickListeners()
    {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (infoWindow != null){
                    infoWindow.RemoveInfoWindow();
                    infoWindow = null;
                }

                Airport airport = airportMarkerMap.get(marker);
                Navaid navaid = navaidMarkerMap.get(marker);
                Waypoint waypoint = null;
                if (selectedFlightplan!= null) {
                    waypoint = selectedFlightplan.waypointMarkerMap.get(marker);
                }
                if (airport != null) {
                    AirportInfoWndFragment airportInfoFragment = new AirportInfoWndFragment();
                    airportInfoFragment.SetAirport(airport, mainActivity);
                    infoWindow = new InfoWindow(marker.getPosition(), googleMap, FSPMapFragment.this, airportInfoFragment);
                    infoWindow.MapPositionChanged();
                }

                if (navaid != null) {
                    NavaidInfoWindowFragment navaidInfoFragment = new NavaidInfoWindowFragment();
                    navaidInfoFragment.SetNavaid(navaid, mainActivity);
                    infoWindow = new InfoWindow(marker.getPosition(), googleMap, FSPMapFragment.this, navaidInfoFragment);
                    infoWindow.MapPositionChanged();
                }

                if (waypoint != null){
                    Log.i(TAG, "Waypoint: " + waypoint.toString());
                    WaypointFragment waypointFragment = new WaypointFragment();
                    waypointFragment.setupWaypoint(waypoint);
                    waypointFragment.SetActivity(mainActivity);
                    waypointFragment.SetOnWaypointListener(new UpdateWaypointListener() {
                        @Override
                        public void OnDeleteWaypoint(Waypoint waypoint) {
                            Log.i(TAG, "Delete waypoint clicked");
                            infoWindow.RemoveInfoWindow();
                            infoWindow = null;
                            flightplanController.DeleteWaypoint(waypoint);
                        }

                        @Override
                        public void OnMoveUpWaypoint(Waypoint waypoint) {
                            infoWindow.RemoveInfoWindow();
                            infoWindow = null;
//                            selectedFlightplan.RemoveFlightplanTrack();
//                            flightplanController.MoveWaypoint(selectedFlightplan, waypoint, false);
                        }

                        @Override
                        public void OnMoveDownWaypoint(Waypoint waypoint) {
                            infoWindow.RemoveInfoWindow();
                            infoWindow = null;
//                            selectedFlightplan.RemoveFlightplanTrack();
//                            flightplanController.MoveWaypoint(selectedFlightplan, waypoint, false);
                        }

                        @Override
                        public void OnRenameWaypoint(Waypoint waypoint, String newName) {
                            infoWindow.RemoveInfoWindow();
                            infoWindow = null;
                            flightplanController.RenameWaypoint(waypoint, newName);
                        }

                        @Override
                        public void OnCloseWaypointWindow()
                        {
                            infoWindow.RemoveInfoWindow();
                            infoWindow = null;
                        }
                    });
                    infoWindow = new InfoWindow(marker.getPosition(), googleMap, FSPMapFragment.this, waypointFragment);
                    infoWindow.MapPositionChanged();
                }

                if (marker.getTag()=="deviationMarker")
                    ShowAirspacesInfoLayout();

                return true;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (infoWindow != null){
                    infoWindow.RemoveInfoWindow();
                    infoWindow = null;
                }
            }
        });
    }

    private void setOnInfoWindowListeners()
    {
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            private Marker marker;
            private Airport airport;
            private Navaid navaid;
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent startWeatherActivity = new Intent(mainActivity, WeatherActivity.class);
                airport = airportMarkerMap.get(marker);
                navaid = navaidMarkerMap.get(marker);
                this.marker = marker;

                if (airport !=null) {
                    startWeatherActivity.putExtra("airport_id", (airport == null) ? -1 : airport.id);
                    mainActivity.startActivityForResult(startWeatherActivity, 500);
                }
            }
        });

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            private View view;
            private Marker marker;
            private Airport airport;
            private Navaid navaid;
            private Waypoint waypoint;
            @Override
            public View getInfoWindow(Marker marker) {
                airport = airportMarkerMap.get(marker);
                navaid = navaidMarkerMap.get(marker);
                if (selectedFlightplan != null)
                    if (selectedFlightplan.waypointMarkerMap != null)
                        waypoint = selectedFlightplan.waypointMarkerMap.get(marker);

                this.marker = marker;

                if (airport != null)
                {
                    this.view = mainActivity.getLayoutInflater().inflate(
                            R.layout.airport_info_window, null);
                    setUpAirportWindow();
                }

                if (navaid != null)
                {
                    this.view = mainActivity.getLayoutInflater().inflate(
                            R.layout.navaid_info_window, null);
                    setUpNavaidWindow();
                }
                if (waypoint != null)
                {
                    this.view = mainActivity.getLayoutInflater().inflate(
                            R.layout.waypoint_info_window, null);
                    setupWaypointWindow();
                }
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            private void setupWaypointWindow()
            {
                if (waypoint != null)
                {
                    TextView infoTxt = (TextView) view.findViewById(R.id.infoWindowWaypointInfoTxt);
                    String info = waypoint.getWaypointInfo();
                    infoTxt.setText(info);
                }
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

                    RunwaysDataSource runwaysDataSource = new RunwaysDataSource(mainActivity);
                    runwaysDataSource.open();
                    airport.runways = runwaysDataSource.loadRunwaysByAirport(airport);
                    runwaysDataSource.close();

                    FrequenciesDataSource frequenciesDataSource = new FrequenciesDataSource(mainActivity);
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

    public void SetupTrackingLine()
    {
        trackingLine = new TrackingLine(googleMap, selectedFlightplan, mainActivity);
    }

    public void SetNewTrackingLinePosition(Location location)
    {
        if (trackingLine != null)
        {
            trackingLine.SetTrackPoints(location);
        }
    }

    public void LoadPreviousTrack(Integer trackId)
    {
        if (loadTrack != null) loadTrack.removeTrack();
        loadTrack = new LoadTrack(mainActivity, googleMap, trackId);
    }

    public ArrayList<LocationTrackingDataSource.TrackPoint> GetPreviousTrackpoints()
    {
        if (loadTrack != null) return loadTrack.getTrackPoints();
        else return null;
    }

    public void RemovePreviousTrack()
    {
        loadTrack.removeTrack();
        loadTrack = null;
    }

    private void showNewWaypointPopup(LatLng Location)
    {
        LinearLayout viewGroup = (LinearLayout) mainActivity.findViewById(R.id.addWaypointPopup);
        LayoutInflater layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.add_waypoint, viewGroup);

        final AddWayPointPopup addWayPointPopup = new AddWayPointPopup(mainActivity, Layout);

        addWayPointPopup.Location = Location;
        addWayPointPopup.setContentView(Layout);
        addWayPointPopup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        addWayPointPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        addWayPointPopup.setFocusable(true);

        addWayPointPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.i(TAG, "AddWayPoint popup dismissed");
                if (addWayPointPopup.Result)
                {
                    selectedFlightplan.RemoveAllRunwayMarkers();
                    selectedFlightplan.removeOldFlightplanMarkers();
                    selectedFlightplan.RemoveFlightplanTrack();
                    //selectedFlightplan.RemoveBuffer();

                    setupNewWaypointInFlightplan(addWayPointPopup.WaypointName,
                            addWayPointPopup.Location.latitude,
                            addWayPointPopup.Location.longitude,
                            WaypointType.userwaypoint,
                            -1);
                }
                if (addWayPointPopup.Search)
                {
                    showSearchPopup();
                }
            }
        });

        addWayPointPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
    }

    private void showSearchPopup()
    {
        int popupWidth = (int) nl.robenanita.googlemapstest.Helpers.convertPixelsToDp(800f, mainActivity);
        int popupHeight = (int) nl.robenanita.googlemapstest.Helpers.convertPixelsToDp(350f, mainActivity);

        LinearLayout viewGroup = null;
        LayoutInflater layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.searchpopup, viewGroup);

        final SearchPopup searchPopup = new SearchPopup(mainActivity, Layout);

        searchPopup.setContentView(Layout);
        searchPopup.setWidth(popupWidth);
        searchPopup.setHeight(popupHeight);
        searchPopup.setFocusable(true);

        searchPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (searchPopup.Result)
                {
//                    Airport a = searchAirportsPopup.SelectedAirport;
//                    LatLng planePos = new LatLng(a.latitude_deg, a.longitude_deg);
//                    curPosition = planePos;
//                    fspMapFragment.SetMapPosition(planePos);
                }
            }
        });

        searchPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
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

        selectedFlightplan.removeOldFlightplanMarkers();
        selectedFlightplan.InsertWaypoint(waypoint);

        RouteDataSource flightPlanDataSource = new RouteDataSource(mainActivity);
        flightPlanDataSource.open();
        flightPlanDataSource.UpdateInsertWaypoints(selectedFlightplan.Waypoints);
        flightPlanDataSource.updateWaypointSortOrder(selectedFlightplan);
        flightPlanDataSource.updateWaypointSortOrderDB(selectedFlightplan);
        flightPlanDataSource.close();


        selectedFlightplan.LoadRunways(googleMap);
        selectedFlightplan.redrawBuffer = true;
        ((NavigationActivity)mainActivity).createBufferArea(curPosition.target);
        selectedFlightplan.DrawFlightplan(googleMap);
        selectedFlightplan.ShowFlightplanMarkers(googleMap);
        LoadFlightplanGrid();
    }

    AviationMarkers aviationMarkers;
    public void SetAviationMarkersByZoomAndBoundary()
    {
        if (aviationMarkers == null) {
            aviationMarkers = new AviationMarkers(mainActivity, googleMap, uniqueID ,airports,
                    navaids, airportMarkerMap, navaidMarkerMap, markerProperties);
            aviationMarkers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //aviationMarkers.execute();
        }
        else {
            aviationMarkers.cancel(true);
            aviationMarkers = new AviationMarkers(mainActivity, googleMap, uniqueID ,airports,
                    navaids, airportMarkerMap, navaidMarkerMap, markerProperties);
            aviationMarkers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //aviationMarkers.execute();
        }
    }

    public void LoadFlightplan(Integer flightplan_id)
    {
        if (selectedFlightplan != null)
        {
            selectedFlightplan.RemoveAllRunwayMarkers();
            selectedFlightplan.removeOldFlightplanMarkers();
            //selectedFlightplan.RemoveBuffer();
            selectedFlightplan.RemoveFlightplanTrack();
            selectedFlightplan = null;
            flightplanController = null;
        }

        selectedFlightplan = new Route(mainActivity);
        selectedFlightplan.LoadFlightplan(mainActivity, flightplan_id, uniqueID);

        Log.i(TAG, "Selected flightplan: " + selectedFlightplan.name);

        selectedFlightplan.UpdateWaypointsData();

        selectedFlightplan.LoadRunways(googleMap);

        selectedFlightplan.ShowFlightplanMarkers(googleMap);
        selectedFlightplan.DrawFlightplan(googleMap);
        LoadFlightplanGrid();

        //selectedFlightplan.DrawBuffer(googleMap);
        selectedFlightplan.redrawBuffer = true;
        ((NavigationActivity)mainActivity).createBufferArea(curPosition.target);

//        AirportDataSource airportDataSource = new AirportDataSource(mainActivity);
//        airportDataSource.open(uniqueID);
//        airportDataSource.getAirportsInBuffer(selectedFlightplan.buffer);
//        airportDataSource.close();

        flightplanController = new RouteController(mainActivity, selectedFlightplan);
        setupFlightplanControllerListeners();

        setupFlightplanListeners(selectedFlightplan);
    }

    private void setupFlightplanListeners(Route flightPlan)
    {
        flightPlan.setOnNewActiveWaypoint(new Route.OnNewActiveWaypoint() {
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
                waypoint.activeCircle = googleMap.addCircle(circleOptions);
            }
        });
    }

    private void setupFlightplanControllerListeners()
    {
        flightplanController.SetOnFlightplanEvent(new OnRouteEvent() {
            @Override
            public void onVariationClicked(Waypoint waypoint, Route route) {
                flightplanGrid.ReloadFlightplan();
                route.RemoveFlightplanTrack();
                route.DrawFlightplan(googleMap);
            }

            @Override
            public void onDeviationClicked(Waypoint waypoint, Route route) {
                flightplanGrid.ReloadFlightplan();
                route.RemoveFlightplanTrack();
                route.DrawFlightplan(googleMap);
            }

            @Override
            public void onTakeoffClicked(Waypoint waypoint, Route route) {
                flightplanGrid.ReloadFlightplan();
                legInfoView.setActiveLeg(route.getActiveLeg(), LegInfoView.Distance.larger2000Meters);
                infoPanel.setActiveLeg(route.getActiveLeg());
            }

            @Override
            public void onAtoClicked(Waypoint waypoint, Route route) {
                flightplanGrid.ReloadFlightplan();
                legInfoView.setActiveLeg(route.getActiveLeg(), LegInfoView.Distance.larger2000Meters);
                infoPanel.setActiveLeg(route.getActiveLeg());
            }

            @Override
            public void onDeleteClickedClicked(Waypoint waypoint, Route route) {
                Integer id = route.id;
                FSPMapFragment.this.closeFlightplan();
                FSPMapFragment.this.LoadFlightplan(id);
            }

            @Override
            public void onClosePlanClicked(Route route) {
                FSPMapFragment.this.closeFlightplan();
            }

            @Override
            public void onWaypointClicked(Waypoint waypoint) {
                LatLng pos = new LatLng(waypoint.location.getLatitude(), waypoint.location.getLongitude());
                FSPMapFragment.this.SetMapPosition(pos);
            }

            @Override
            public void onWaypointMoved(Route route) {
                route.removeOldFlightplanMarkers();
                route.UpdateWaypointsData();
                route.DrawFlightplan(googleMap);
                route.ShowFlightplanMarkers(googleMap);
            }

            @Override
            public void onWaypointRenamed(Waypoint waypoint, String newName)
            {
                flightplanGrid.ReloadFlightplan();
            }
        });
    }

    private void closeFlightplan()
    {
        setFlightplanLayout(false);

        if (selectedFlightplan != null)
        {
            selectedFlightplan.removeOldFlightplanMarkers();
            selectedFlightplan.RemoveFlightplanTrack();
            selectedFlightplan.RemoveAllRunwayMarkers();
            //selectedFlightplan.RemoveBuffer();

            selectedFlightplan = null;

            ((NavigationActivity)mainActivity).createBufferArea(googleMap.getCameraPosition().target);
        }
    }

    private void closeFlightplanShowAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
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

        builder.setMessage("Are you sure you want to close this Route?");
        builder.setTitle("Close Flightplan?");

        AlertDialog closePlanDialog = builder.create();
        closePlanDialog.show();
    }

    private RouteGrid flightplanGrid;
    private void LoadFlightplanGrid()
    {
        //setFlightplanLayout(true);

        RouteGrid tempGrid = (RouteGrid) getChildFragmentManager().findFragmentById(R.id.fspflightplanFragment);
        if (tempGrid==null) tempGrid = (RouteGrid) getFragmentManager().findFragmentById(R.id.fspflightplanFragment);

        flightplanGrid = tempGrid;

        flightplanGrid.setOnFlightplanEvent(new OnRouteEvent() {
            @Override
            public void onVariationClicked(Waypoint waypoint, Route flightPlan) {
                LinearLayout flightplanLayout = (LinearLayout) getView().findViewById(R.id.fspflightplanLayout);
                flightplanController.SetVariation(waypoint);
            }
            @Override
            public void onDeviationClicked(Waypoint waypoint, Route flightPlan) {
                flightplanController.SetDeviation(waypoint);
            }
            @Override
            public void onTakeoffClicked(Waypoint waypoint, Route flightPlan) {
                flightplanController.SetETO(waypoint, curPlaneLocation);
            }

            @Override
            public void onAtoClicked(Waypoint waypoint, Route flightPlan) {
                flightplanController.SetATO(waypoint, curPlaneLocation);
            }

            @Override
            public void onDeleteClickedClicked(Waypoint waypoint, Route flightPlan) {
                flightplanController.DeleteWaypoint(waypoint);
            }

            @Override
            public void onClosePlanClicked(Route flightPlan) {
                closeFlightplanShowAlert();
            }

            @Override
            public void onWaypointClicked(Waypoint waypoint) {
                LatLng pos = new LatLng(waypoint.location.getLatitude(), waypoint.location.getLongitude());
                FSPMapFragment.this.SetMapPosition(pos);
            }

            @Override
            public void onWaypointMoved(Route flightPlan) {
                flightplanController.MoveWaypoint2(flightPlan);
            }

            @Override
            public void onWaypointRenamed(Waypoint waypoint, String newName)
            {
                flightplanController.RenameWaypoint(waypoint, newName);
            }


        });

        flightplanGrid.LoadFlightplanGrid(selectedFlightplan);
    }

    public void SetATO(Waypoint waypoint, Location curPlaneLocation)
    {
        flightplanController.SetATO(waypoint, curPlaneLocation);
    }

    public void SetupDirectToTrack(Airport airport, Context context)
    {
        if (track != null)
        {
            track.RemoveTrack();
            track = null;
        }

        track = new Track(context);

        Location l = curPlaneLocation;

        if (selectedFlightplan != null) {
            Leg alternateLeg = track.getDirecttoLeg(selectedFlightplan, l, context);
            selectedFlightplan.setActiveLeg(alternateLeg);
            alternateLeg.DrawLeg(googleMap, Color.RED);
        }
        else
        {
            Leg alternateLeg = track.getDirecttoLeg(l, airport, context);
            alternateLeg.DrawLeg(googleMap, Color.RED);
            alternateLeg.SetCoarseMarker(googleMap, context);
        }

        infoPanel.setTrack(track);

        //SetInfoPanel(l);
    }

    public void SetupDirectToTrack(Navaid navaid, Context context)
    {
        if (track != null)
        {
            track.RemoveTrack();
            track = null;
        }

        track = new Track(context);

        Location l = curPlaneLocation;

        Leg alternateLeg = track.getDirecttoLeg(l, navaid, context);
        alternateLeg.DrawLeg(googleMap, Color.RED);
        alternateLeg.SetCoarseMarker(googleMap, context);

        infoPanel.setTrack(track);

        //SetInfoPanel(l);
    }

    public void SetupDirectToTrack(Fix fix, Context context)
    {
        if (track != null)
        {
            track.RemoveTrack();
            track = null;
        }

        track = new Track(context);

        Location l = curPlaneLocation;

        Leg alternateLeg = track.getDirecttoLeg(l, fix, context);
        alternateLeg.DrawLeg(googleMap, Color.RED);
        alternateLeg.SetCoarseMarker(googleMap, context);

        infoPanel.setTrack(track);

        //SetInfoPanel(l);
    }

    private AirspaceInfoFragment airspaceInfoFragment;
    public void ShowAirspacesInfoLayout()
    {
        final LinearLayout airspacesInfoLayout = (LinearLayout) getView().findViewById(R.id.airspacesInfoLayout);
        airspaceInfoFragment =
                (AirspaceInfoFragment) getFragmentManager().findFragmentById(R.id.airspacesInfoFragment);
        if (airspaceInfoFragment == null)
            airspaceInfoFragment = (AirspaceInfoFragment) this.getChildFragmentManager().findFragmentById(R.id.airspacesInfoFragment);
        airspaceInfoFragment.SetOnAirspaceClicked(new AirspaceInfoFragment.OnAirspaceClicked() {
            @Override
            public void AirspaceClicked(Airspace airspace, Airspaces airspaces) {
                airspacesInfoLayout.setVisibility(View.INVISIBLE);
                airspaces.removeAirspacesLayer();
            }
        });

        airspaceInfoFragment.LoadAirspacesForLocation(googleMap, mainActivity);
        airspacesInfoLayout.setVisibility(View.VISIBLE);
    }

}

package nl.robenanita.googlemapstest.MapFragment;


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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SlidingDrawer;
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
import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Classes.PlanePosition;
import nl.robenanita.googlemapstest.Fix;
import nl.robenanita.googlemapstest.FlightplanGrid;
import nl.robenanita.googlemapstest.InfoPanelFragment;
import nl.robenanita.googlemapstest.InfoWindows.AirportInfoWndFragment;
import nl.robenanita.googlemapstest.InfoWindows.UpdateWaypointListener;
import nl.robenanita.googlemapstest.InfoWindows.NavaidInfoWindowFragment;
import nl.robenanita.googlemapstest.InfoWindows.WaypointFragment;
import nl.robenanita.googlemapstest.LegInfoView;
import nl.robenanita.googlemapstest.MapController;
import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Settings.LayersSetup.MapStyle;
import nl.robenanita.googlemapstest.Track;
import nl.robenanita.googlemapstest.Tracks.LoadTrack;
import nl.robenanita.googlemapstest.Weather.WeatherActivity;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.FlightPlanDataSource;
import nl.robenanita.googlemapstest.database.FrequenciesDataSource;
import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;
import nl.robenanita.googlemapstest.database.MarkerProperties;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;
import nl.robenanita.googlemapstest.flightplan.DragLine;
import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.FlightplanController;
import nl.robenanita.googlemapstest.flightplan.Leg;
import nl.robenanita.googlemapstest.flightplan.OnFlightplanEvent;
import nl.robenanita.googlemapstest.flightplan.Waypoint;
import nl.robenanita.googlemapstest.flightplan.WaypointType;
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

    private FlightPlan selectedFlightplan;
    private TrackingLine trackingLine;

    private Leg clickedLeg;

    private LoadTrack loadTrack;

    private FlightplanController flightplanController;

    private LegInfoView legInfoView;
    private InfoPanelFragment infoPanel;

    private InfoWindow infoWindow;
    private SlidingDrawer flightplangriddrawer;

    private Track track;

    private NewWaypointFragment newWaypointFragment;

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

    public void InitializeMap(Activity mainActivity, LegInfoView legInfoView, InfoPanelFragment infoPanelFragment)
    {
        this.mainActivity = mainActivity;
        this.legInfoView = legInfoView;
        this.infoPanel = infoPanelFragment;
        setupMap();

    }

    private void SetupDrawerListeners() {

        flightplangriddrawer = (SlidingDrawer) this.getView().findViewById(R.id.fspflightplandrawer);

        flightplangriddrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                FlightplanGrid flightplanggrid = (FlightplanGrid)FSPMapFragment.this.getFragmentManager().findFragmentById(R.id.fspflightplanFragment);
                Log.i(TAG, "grid height : ");
            }
        });

//        flightplangriddrawer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FlightplanGrid flightplanggrid = (FlightplanGrid)FSPMapFragment.this.getFragmentManager().findFragmentById(R.id.fspflightplanFragment);
//                Log.i(TAG, "grid height : " + flightplanggrid.getView().getMeasuredHeight());
//            }
//        });
    }

    public GoogleMap GetGooglemap()
    {
        return googleMap;
    }

    public FlightPlan GetCurrentFlightplan()
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
        float zoom = googleMap.getCameraPosition().zoom;
        curPosition = new CameraPosition(position, zoom,0,0);
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(curPosition));
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
                SetAviationMarkersByZoomAndBoundary();

                if (newWaypointFragment.isVisible())
                {
                    newWaypointFragment.setNewCameraPosition(googleMap, FSPMapFragment.this.mainActivity);
                }

                if (onCameraIdleListener != null)
                    FSPMapFragment.this.onCameraIdleListener.onCameraIdle();
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
                Log.i(TAG, "Polyline clicked");
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

    private void setNewWaypointInFlightplan(SelectableWaypoint waypoint)
    {
        if (selectedFlightplan != null){
            if (!selectedFlightplan.getFlightplanActive())
            {
                selectedFlightplan.RemoveAllRunwayMarkers();
                selectedFlightplan.removeOldFlightplanMarkers();
                selectedFlightplan.RemoveFlightplanTrack();
                selectedFlightplan.RemoveBuffer();

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
                        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(mainActivity);
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
                Waypoint waypoint = selectedFlightplan.waypointMarkerMap.get(marker);

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
                            selectedFlightplan.RemoveFlightplanTrack();
                            flightplanController.MoveWaypoint(selectedFlightplan, waypoint, false);
                        }

                        @Override
                        public void OnMoveDownWaypoint(Waypoint waypoint) {
                            infoWindow.RemoveInfoWindow();
                            infoWindow = null;
                            selectedFlightplan.RemoveFlightplanTrack();
                            flightplanController.MoveWaypoint(selectedFlightplan, waypoint, false);
                        }

                        @Override
                        public void OnRenameWaypoint(Waypoint waypoint, String newName) {
                            infoWindow.RemoveInfoWindow();
                            infoWindow = null;
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
                    selectedFlightplan.RemoveBuffer();

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

        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(mainActivity);
        flightPlanDataSource.open();
        flightPlanDataSource.UpdateInsertWaypoints(selectedFlightplan.Waypoints);
        flightPlanDataSource.updateWaypointSortOrder(selectedFlightplan);
        flightPlanDataSource.updateWaypointSortOrderDB(selectedFlightplan);
        flightPlanDataSource.close();


        selectedFlightplan.LoadRunways(googleMap);
        selectedFlightplan.DrawBuffer(googleMap);
        //PlaceFlightplanAirportMarkers();
        //LoadFlightplanRunways();
        selectedFlightplan.DrawFlightplan(googleMap);
        selectedFlightplan.ShowFlightplanMarkers(googleMap, mainActivity);
        LoadFlightplanGrid();
    }

    AviationMarkers aviationMarkers;
    public void SetAviationMarkersByZoomAndBoundary()
    {
        if (aviationMarkers == null) {
            aviationMarkers = new AviationMarkers(mainActivity, googleMap, uniqueID ,airports,
                    navaids, airportMarkerMap, navaidMarkerMap, markerProperties);
            aviationMarkers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            aviationMarkers.cancel(true);
            aviationMarkers = new AviationMarkers(mainActivity, googleMap, uniqueID ,airports,
                    navaids, airportMarkerMap, navaidMarkerMap, markerProperties);
            aviationMarkers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void LoadFlightplan(Integer flightplan_id)
    {
        if (selectedFlightplan != null)
        {
            selectedFlightplan.RemoveAllRunwayMarkers();
            selectedFlightplan.removeOldFlightplanMarkers();
            selectedFlightplan.RemoveBuffer();
            selectedFlightplan.RemoveFlightplanTrack();
            selectedFlightplan = null;
            flightplanController = null;
        }

        selectedFlightplan = new FlightPlan(mainActivity);
        selectedFlightplan.LoadFlightplan(mainActivity, flightplan_id, uniqueID);

        Log.i(TAG, "Selected flightplan: " + selectedFlightplan.name);

        selectedFlightplan.UpdateWaypointsData();

        selectedFlightplan.LoadRunways(googleMap);

        selectedFlightplan.ShowFlightplanMarkers(googleMap, mainActivity);
        selectedFlightplan.DrawFlightplan(googleMap);
        //SetupFlightplanListeners(selectedFlightplan);
        LoadFlightplanGrid();
        //PlaceFlightplanAirportMarkers();

        selectedFlightplan.DrawBuffer(googleMap);

        AirportDataSource airportDataSource = new AirportDataSource(mainActivity);
        airportDataSource.open(uniqueID);
        airportDataSource.getAirportsInBuffer(selectedFlightplan.buffer);
        airportDataSource.close();

        flightplanController = new FlightplanController(mainActivity, selectedFlightplan);
        setupFlightplanControllerListeners();

        setupFlightplanListeners(selectedFlightplan);
    }

    private void setupFlightplanListeners(FlightPlan flightPlan)
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
                waypoint.activeCircle = googleMap.addCircle(circleOptions);
            }
        });
    }

    private void setupFlightplanControllerListeners()
    {
        flightplanController.SetOnFlightplanEvent(new OnFlightplanEvent() {
            @Override
            public void onVariationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanGrid.ReloadFlightplan();
                flightPlan.RemoveFlightplanTrack();
                flightPlan.DrawFlightplan(googleMap);
            }

            @Override
            public void onDeviationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanGrid.ReloadFlightplan();
                flightPlan.RemoveFlightplanTrack();
                flightPlan.DrawFlightplan(googleMap);
            }

            @Override
            public void onTakeoffClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanGrid.ReloadFlightplan();
                //legInfoView.setVisibility(View.VISIBLE);
                legInfoView.setActiveLeg(flightPlan.getActiveLeg(), LegInfoView.Distance.larger2000Meters);
                infoPanel.setActiveLeg(flightPlan.getActiveLeg());
            }

            @Override
            public void onAtoClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanGrid.ReloadFlightplan();
                legInfoView.setActiveLeg(flightPlan.getActiveLeg(), LegInfoView.Distance.larger2000Meters);
                infoPanel.setActiveLeg(flightPlan.getActiveLeg());
            }

            @Override
            public void onMoveUpClicked(Waypoint waypoint, FlightPlan flightPlan) {
                Integer id = flightPlan.id;
                FSPMapFragment.this.closeFlightplan();
                FSPMapFragment.this.LoadFlightplan(id);
            }

            @Override
            public void onMoveDownClicked(Waypoint waypoint, FlightPlan flightPlan) {
                Integer id = flightPlan.id;
                FSPMapFragment.this.closeFlightplan();
                FSPMapFragment.this.LoadFlightplan(id);
            }

            @Override
            public void onDeleteClickedClicked(Waypoint waypoint, FlightPlan flightPlan) {
                Integer id = flightPlan.id;
                FSPMapFragment.this.closeFlightplan();
                FSPMapFragment.this.LoadFlightplan(id);
            }

            @Override
            public void onClosePlanClicked(FlightPlan flightPlan) {
                FSPMapFragment.this.closeFlightplan();
            }
        });
    }

    private void closeFlightplan()
    {
        LinearLayout flightplanLayout = (LinearLayout) getView().findViewById(R.id.fspflightplanLayout);
        flightplanLayout.setVisibility(View.GONE);
        SlidingDrawer flightplanDrawer = (SlidingDrawer) getView().findViewById(R.id.fspflightplandrawer);
        flightplanDrawer.setVisibility(View.GONE);

        if (selectedFlightplan != null)
        {
            selectedFlightplan.removeOldFlightplanMarkers();
            selectedFlightplan.RemoveFlightplanTrack();
            selectedFlightplan.RemoveAllRunwayMarkers();
            selectedFlightplan.RemoveBuffer();

            //legInfoView.setVisibility(View.GONE);
            selectedFlightplan = null;
            //if (track != null) track.RemoveTrack();
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

        builder.setMessage("Are you sure you want to close this FlightPlan?");
        builder.setTitle("Close Flightplan?");

        AlertDialog closePlanDialog = builder.create();
        closePlanDialog.show();
    }

    private FlightplanGrid flightplanGrid;
    private void LoadFlightplanGrid()
    {
        LinearLayout flightplanLayout = (LinearLayout) getView().findViewById(R.id.fspflightplanLayout);
        flightplanLayout.setVisibility(View.VISIBLE);
        SlidingDrawer flightplanDrawer = (SlidingDrawer) getView().findViewById(R.id.fspflightplandrawer);
        flightplanDrawer.setVisibility(View.VISIBLE);

        FlightplanGrid tempGrid = (FlightplanGrid) getChildFragmentManager().findFragmentById(R.id.fspflightplanFragment);
        if (tempGrid==null) tempGrid = (FlightplanGrid) getFragmentManager().findFragmentById(R.id.fspflightplanFragment);

        flightplanGrid = tempGrid;

        flightplanGrid.setOnFlightplanEvent(new OnFlightplanEvent() {
            @Override
            public void onVariationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                LinearLayout flightplanLayout = (LinearLayout) getView().findViewById(R.id.fspflightplanLayout);
                flightplanController.SetVariation(waypoint);
            }
            @Override
            public void onDeviationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanController.SetDeviation(waypoint);
            }
            @Override
            public void onTakeoffClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanController.SetETO(waypoint, curPlaneLocation);
            }

            @Override
            public void onAtoClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanController.SetATO(waypoint, curPlaneLocation);
            }

            @Override
            public void onMoveUpClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //moveWaypoint(flightPlan,waypoint, false);
                Log.i(TAG, "Move Up Clicked");
                flightPlan.RemoveFlightplanTrack();
                flightplanController.MoveWaypoint(flightPlan, waypoint, false);
            }

            @Override
            public void onMoveDownClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //moveWaypoint(flightPlan,waypoint, true);
                flightPlan.RemoveFlightplanTrack();
                flightplanController.MoveWaypoint(flightPlan, waypoint, true);
            }

            @Override
            public void onDeleteClickedClicked(Waypoint waypoint, FlightPlan flightPlan) {
                flightplanController.DeleteWaypoint(waypoint);
            }

            @Override
            public void onClosePlanClicked(FlightPlan flightPlan) {
                closeFlightplanShowAlert();
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

}

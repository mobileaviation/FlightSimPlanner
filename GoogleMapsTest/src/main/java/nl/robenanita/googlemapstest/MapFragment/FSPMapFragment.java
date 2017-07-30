package nl.robenanita.googlemapstest.MapFragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
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

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.robenanita.googlemapstest.AddWayPointPopup;
import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Classes.PlanePosition;
import nl.robenanita.googlemapstest.FlightplanGrid;
import nl.robenanita.googlemapstest.MapController;
import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Tracks.LoadTrack;
import nl.robenanita.googlemapstest.Weather.WeatherActivity;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.FlightPlanDataSource;
import nl.robenanita.googlemapstest.database.FrequenciesDataSource;
import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;
import nl.robenanita.googlemapstest.database.MarkerProperties;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;
import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.Leg;
import nl.robenanita.googlemapstest.flightplan.Waypoint;
import nl.robenanita.googlemapstest.flightplan.WaypointType;
import nl.robenanita.googlemapstest.markers.AviationMarkers;
import nl.robenanita.googlemapstest.markers.PlaneMarker;
import nl.robenanita.googlemapstest.search.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FSPMapFragment extends Fragment {

    private final String TAG = "FSPMapFragment";
    private GoogleMap googleMap;

    private OnMapReadyCallback onMapReadyCallback;
    public void SetOnMapReadyCallback(OnMapReadyCallback onMapReadyCallback)
    { this.onMapReadyCallback = onMapReadyCallback; }

    private Activity mainActivity;

    private CameraPosition curPosition;
    private PlaneMarker planeMarker;

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

    public FSPMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fspmap, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void InitializeMap(Activity mainActivity)
    {
        this.mainActivity = mainActivity;
        setupMap();
    }

    private void setupMap()
    {
        final MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_fsp_map);

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
                setOnInfoWindowListeners();
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
        mapController.setBaseMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mapController.setUpTileProvider();
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
                Log.i(TAG, "Camera Moved to: " + googleMap.getCameraPosition().target.latitude + " : " + googleMap.getCameraPosition().target.longitude);
                curPosition = googleMap.getCameraPosition();
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
                clickedLeg = (Leg)polyline.getTag();
                LatLng midwaypoint = nl.robenanita.googlemapstest.Helpers.midPoint(polyline.getPoints().get(0), polyline.getPoints().get(1));
                Log.i(TAG, "Midway Position Lat: " + midwaypoint.latitude + " Lon: " + midwaypoint.longitude);
                showNewWaypointPopup(midwaypoint);
            }
        });
    }

    private void setOnMarkerDragListeners()
    {
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            private Polyline dragLine;
            @Override
            public void onMarkerDragStart(Marker marker) {
                Waypoint w = selectedFlightplan.waypointMarkerMap.get(marker);
                Waypoint beforeWaypoint = selectedFlightplan.getBeforeWaypoint(w);
                Waypoint afterWaypoint = selectedFlightplan.getAfterWaypoint(w);
                PolylineOptions options = new PolylineOptions();
                options.color(Color.RED);
                options.width(7);
                options.zIndex(1001);
                options.add(new LatLng(beforeWaypoint.location.getLatitude(), beforeWaypoint.location.getLongitude()));
                options.add(new LatLng(w.location.getLatitude(), w.location.getLongitude()));
                options.add(new LatLng(afterWaypoint.location.getLatitude(), afterWaypoint.location.getLongitude()));
                dragLine = googleMap.addPolyline(options);
            }
            @Override
            public void onMarkerDrag(Marker marker) {
                List<LatLng> points = dragLine.getPoints();
                points.set(1, marker.getPosition());
                dragLine.setPoints(points);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (dragLine != null) {
                    dragLine.remove();
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
                    Intent searchIntent = new Intent(mainActivity, SearchActivity.class);
                    searchIntent.putExtra("key", 1);
                    Bundle b = new Bundle();
                    b.putParcelable("location", curPosition);
                    searchIntent.putExtra("location", b);
                    mainActivity.startActivityForResult(searchIntent, 200);
                }
            }
        });

        addWayPointPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
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

    private void LoadFlightplanGrid()
    {
        LinearLayout flightplanLayout = (LinearLayout) getView().findViewById(R.id.fspflightplanLayout);
        flightplanLayout.setVisibility(View.VISIBLE);
        SlidingDrawer flightplanDrawer = (SlidingDrawer) getView().findViewById(R.id.fspflightplandrawer);
        flightplanDrawer.setVisibility(View.VISIBLE);

        FlightplanGrid flightplanGrid = (FlightplanGrid) getFragmentManager().findFragmentById(R.id.fspflightplanFragment);
        flightplanGrid.setOnFlightplanEvent(new FlightplanGrid.OnFlightplanEvent() {
            @Override
            public void onVariationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //VariationClick(waypoint);
            }

            @Override
            public void onDeviationClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //DeviationClick(waypoint);
            }

            @Override
            public void onTakeoffClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //ETOClick(waypoint);
            }

            @Override
            public void onAtoClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //ATOClick(waypoint);
            }

            @Override
            public void onMoveUpClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //moveWaypoint(flightPlan,waypoint, false);
            }

            @Override
            public void onMoveDownClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //moveWaypoint(flightPlan,waypoint, true);
            }

            @Override
            public void onDeleteClickedClicked(Waypoint waypoint, FlightPlan flightPlan) {
                //deleteWaypoint(waypoint);
            }

            @Override
            public void onClosePlanClicked(FlightPlan flightPlan) {
                closeFlightplanShowAlert();
            }


        });

        flightplanGrid.LoadFlightplanGrid(selectedFlightplan);
    }
}

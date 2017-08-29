package nl.robenanita.googlemapstest.MapFragment;


import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.NavaidsDataSource;
import nl.robenanita.googlemapstest.flightplan.Waypoint;
import nl.robenanita.googlemapstest.flightplan.WaypointType;

public class NewWaypointFragment extends Fragment {
    private GoogleMap googleMap;
    private Location curLocation;
    private View view;

    private final String TAG = "NewWaypointFragment";

    private NewWapointSelectedListener newWapointSelectedListener;
    public void SetOnNewWaypointSelectedListener(NewWapointSelectedListener newWapointSelectedListener)
    {
        this.newWapointSelectedListener = newWapointSelectedListener;
    }

    public NewWaypointFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_new_waypoint, container, false);
        return view;
    }


    public void setNewCameraPosition(GoogleMap googleMap, Context context)
    {
        this.googleMap = googleMap;
        Log.i(TAG, "Camera position: " + googleMap.getCameraPosition().toString());
        LatLng position = googleMap.getCameraPosition().target;

        Point pointmid = googleMap.getProjection().toScreenLocation(position);
        int dp = Math.round(Helpers.convertDpToPixel(100, context));
        Point lefttop = new Point(pointmid.x-dp, pointmid.y+dp);
        Point bottomright = new Point(pointmid.x+dp, pointmid.y-dp);
        LatLng p1 = googleMap.getProjection().fromScreenLocation(lefttop);
        LatLng p2 = googleMap.getProjection().fromScreenLocation(bottomright);

        LatLngBounds bounds = new LatLngBounds(p1, p2);

        AirportDataSource airportsDatasource = new AirportDataSource(context);
        airportsDatasource.open(0);
        ArrayList<Airport> airports = airportsDatasource.getAirportsByBoundary(bounds);
        airportsDatasource.close();

        NavaidsDataSource navaidsDataSource = new NavaidsDataSource(context);
        navaidsDataSource.open(0);
        ArrayList<Navaid> navaids = navaidsDataSource.GetNaviadsByBoundary(bounds);
        navaidsDataSource.close();


        ArrayList<SelectableWaypoint> waypoints = new ArrayList<>();

        SelectableWaypoint waypoint = new SelectableWaypoint();
        waypoint.ident = "Current Position";
        waypoint.name = "LAT: " + position.latitude + " LON: " + position.longitude;
        waypoint.position = position;
        waypoint.type = WaypointType.userwaypoint;
        waypoints.add(waypoint);

        for (Airport A: airports){
            waypoint = new SelectableWaypoint();
            waypoint.object = A;
            waypoint.type = WaypointType.Airport;
            waypoints.add(waypoint);
        }

        for (Navaid N: navaids){
            waypoint = new SelectableWaypoint();
            waypoint.object = N;
            waypoint.type = WaypointType.navaid;
            waypoints.add(waypoint);
        }


        setupAirportsList(waypoints);
    }

    private void setupAirportsList(ArrayList<SelectableWaypoint> waypoints)
    {
        ListView listView = (ListView) view.findViewById(R.id.addWaypointItemsList);
        AddWaypointAdapter adapter = new AddWaypointAdapter(waypoints);
        listView.setAdapter(adapter);
        setupListViewClickListeners();
    }

    private void setupListViewClickListeners()
    {
        ListView listView = (ListView) view.findViewById(R.id.addWaypointItemsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AddWaypointAdapter waypointAdapter = (AddWaypointAdapter) adapterView.getAdapter();
                SelectableWaypoint waypoint = waypointAdapter.GetSelectedWaypoint(i);
                Log.i(TAG, "Selected: " + waypoint.GetName() + " Loc: " + waypoint.GetLatLng().toString() );
                if (newWapointSelectedListener != null) newWapointSelectedListener.NewWaypointSelected(waypoint);
            }
        });

        ImageButton closeBtn = (ImageButton) view.findViewById(R.id.addWaypointCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newWapointSelectedListener != null) newWapointSelectedListener.NewWaypointCanceled();
            }
        });
    }
}

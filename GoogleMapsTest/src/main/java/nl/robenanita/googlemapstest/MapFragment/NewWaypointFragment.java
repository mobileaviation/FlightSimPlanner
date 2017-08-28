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
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.AirportDataSource;

public class NewWaypointFragment extends Fragment {
    private GoogleMap googleMap;
    private Location curLocation;
    private View view;

    private final String TAG = "NewWaypointFragment";

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

        String airportidensts = "";
        for (Airport A: airports)
            airportidensts = airportidensts + " ," + A.ident;

        Log.i(TAG, "Found airports: " + airportidensts);

        setupAirportsList(airports);
    }

    private void setupAirportsList(ArrayList<Airport> airports)
    {
        ListView listView = (ListView) view.findViewById(R.id.addWaypointItemsList);
        AddWaypointAdapter adapter = new AddWaypointAdapter(airports);
        listView.setAdapter(adapter);
    }
}

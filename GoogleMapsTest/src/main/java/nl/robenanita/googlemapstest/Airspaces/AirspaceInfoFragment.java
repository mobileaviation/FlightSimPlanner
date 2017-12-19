package nl.robenanita.googlemapstest.Airspaces;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;

import nl.robenanita.googlemapstest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AirspaceInfoFragment extends Fragment {

    public interface OnAirspaceClicked
    {
        public void AirspaceClicked(Airspace airspace, Airspaces airspaces);
    }
    private OnAirspaceClicked onAirspaceClicked;
    public void SetOnAirspaceClicked(OnAirspaceClicked onAirspaceClicked)
    {
        this.onAirspaceClicked = onAirspaceClicked;
    }

    public AirspaceInfoFragment() {
        // Required empty public constructor
    }

    private View view;
    private Airspaces airspaces;
    private GoogleMap map;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_airspace_info, container, false);
        return this.view;
    }

    public void LoadAirspacesForLocation(GoogleMap map, Context context)
    {
        removeAirspacesFromMap();
        LatLng location = map.getCameraPosition().target;
        this.map = map;

        Coordinate c = new Coordinate(location.longitude, location.latitude);
        WithinAirspaceCheck withinAirspaceCheck = new WithinAirspaceCheck(context, c);
        withinAirspaceCheck.SetOnFoundAirspace(new WithinAirspaceCheck.OnFoundAirspace() {
            @Override
            public void OnFoundAirspace(Airspace airspace) {

            }

            @Override
            public void OnFoundAllAirspaces(final Airspaces airspaces) {
                AirspaceInfoFragment.this.airspaces = airspaces;
                ListView airspacesListView = (ListView) view.findViewById(R.id.airspacesList);

                airspacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Airspace airspace = (Airspace) adapterView.getAdapter().getItem(i);
                        if (onAirspaceClicked != null) onAirspaceClicked.AirspaceClicked(airspace, airspaces);
                    }
                });

                AirspaceAdapter airspaceAdapter = new AirspaceAdapter(airspaces);
                airspacesListView.setAdapter(airspaceAdapter);
                airspaces.createAirspacesLayer(AirspaceInfoFragment.this.map);
            }
        });
        withinAirspaceCheck.execute();
    }

    public void removeAirspacesFromMap()
    {
        if (airspaces != null)
            airspaces.removeAirspacesLayer();
    }

}

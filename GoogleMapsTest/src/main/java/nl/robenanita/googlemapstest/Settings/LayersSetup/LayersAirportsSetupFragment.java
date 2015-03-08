package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import nl.robenanita.googlemapstest.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LayersAirportsSetupFragment extends Fragment {


    public LayersAirportsSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return (LinearLayout) inflater.inflate(R.layout.fragment_layers_airports_setup, container, false);
    }


}

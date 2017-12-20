package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.robenanita.googlemapstest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LayersOfflineSetupFragment extends Fragment {


    public LayersOfflineSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offline_setup, container, false);
    }

}

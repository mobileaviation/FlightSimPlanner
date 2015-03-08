package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.robenanita.googlemapstest.R;

//import android.support.v4.app.FragmentTabHost;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MapLayersFragment extends Fragment {


    public MapLayersFragment() {
        // Required empty public constructor
    }

    private String TAG = "GooglemapsTest";


    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.fragment_map_layers, container, false);


        // create the TabHost that will contain the Tabs
        FragmentTabHost tabHost = new FragmentTabHost(getActivity());
        tabHost.setBackgroundColor(0xEEEEEE);

        tabHost.setup(getActivity(), getChildFragmentManager(), R.id.layersContainer);

        tabHost.addTab(tabHost.newTabSpec("Google Maps").setIndicator("Google Maps"),
                LayersBaseSetupFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("AAF-US Maps").setIndicator("AAF-US Maps"),
                LayersUsSetupFragment.class, null);
//        tabHost.addTab(tabHost.newTabSpec("Airports").setIndicator("Airports"),
//                LayersAirportsSetupFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("Airspaces").setIndicator("Airspaces"),
                LayersAirspacesSetupFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("Weather").setIndicator("Weather"),
                LayersWeatherSetupFragment.class, null);



//        tabHost.setCurrentTab(0);
//        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String s) {
//
//            }
//        });


        return tabHost;
    }



}

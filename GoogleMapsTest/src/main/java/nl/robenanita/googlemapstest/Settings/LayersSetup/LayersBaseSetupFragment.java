package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;

import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LayersBaseSetupFragment extends Fragment {


    public LayersBaseSetupFragment() {
        // Required empty public constructor
    }

    OnBaseMapTypeChanged onBaseMapTypeChanged = null;
    NavigationActivity n;

    int mapType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = (LinearLayout) inflater.inflate(R.layout.fragment_layers_base_setup, container, false);

        n = (NavigationActivity)container.getContext();

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.baseBtnGroup);

        if (n.mapController != null) {
            Integer typeMap = n.mapController.getGoogleMapType();
            if (typeMap != null) {
                if (typeMap == GoogleMap.MAP_TYPE_HYBRID)
                    radioGroup.check(R.id.layersBaseHybridBtn);
                if (typeMap == GoogleMap.MAP_TYPE_NONE) radioGroup.check(R.id.layersBaseNoneBtn);
                if (typeMap == GoogleMap.MAP_TYPE_NORMAL)
                    radioGroup.check(R.id.layersBaseNormalBtn);
                if (typeMap == GoogleMap.MAP_TYPE_SATELLITE)
                    radioGroup.check(R.id.layersBaseSatelliteBtn);
                if (typeMap == GoogleMap.MAP_TYPE_TERRAIN)
                    radioGroup.check(R.id.layersBaseTerrainBtn);
            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton rb = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());

                    if (rb.getText().equals("None")) mapType = GoogleMap.MAP_TYPE_NONE;
                    if (rb.getText().equals("Normal")) mapType = GoogleMap.MAP_TYPE_NORMAL;
                    if (rb.getText().equals("Satellite")) mapType = GoogleMap.MAP_TYPE_SATELLITE;
                    if (rb.getText().equals("Hybrid")) mapType = GoogleMap.MAP_TYPE_HYBRID;
                    if (rb.getText().equals("Terrain")) mapType = GoogleMap.MAP_TYPE_TERRAIN;

                    if (onBaseMapTypeChanged != null)
                        onBaseMapTypeChanged.onMapTypeChanged(mapType);

                    n.mapController.setMapType(mapType);

                    // Todo: Save settings to DB
                }
            });
        }

        return view;
    }

    public void setOnBaseMapTypeChanged( final OnBaseMapTypeChanged d) {onBaseMapTypeChanged = d; }
    public interface OnBaseMapTypeChanged {
        public void onMapTypeChanged(int mapType);
    }

}

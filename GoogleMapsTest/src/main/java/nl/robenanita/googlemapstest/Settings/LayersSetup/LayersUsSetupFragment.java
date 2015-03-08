package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Wms.TileProviderFormats;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LayersUsSetupFragment extends Fragment {

    NavigationActivity n;
    View view;

    public LayersUsSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = (LinearLayout) inflater.inflate(R.layout.fragment_layers_us_setup, container, false);

        n = (NavigationActivity)container.getContext();

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.usLayersGroup);

        final TileProviderFormats.chartBundleLayer selectedLayer = n.mapController.getSelectedChartBundle();
        if (selectedLayer!=null) {
            if (selectedLayer.equals(TileProviderFormats.chartBundleLayer.sec_4326))
                radioGroup.check(R.id.layersUsSectionalBtn);
            if (selectedLayer.equals(TileProviderFormats.chartBundleLayer.tac_4326))
                radioGroup.check(R.id.layersUsTerminalBtn);
            if (selectedLayer.equals(TileProviderFormats.chartBundleLayer.wac_4326))
                radioGroup.check(R.id.layersUsWorldBtn);
            if (selectedLayer.equals(TileProviderFormats.chartBundleLayer.enrl_4326))
                radioGroup.check(R.id.layersUsEnrouteLowBtn);
            if (selectedLayer.equals(TileProviderFormats.chartBundleLayer.enrh_4326))
                radioGroup.check(R.id.layersUsEnrouteHighBtn);
            if (selectedLayer.equals(TileProviderFormats.chartBundleLayer.enra_4326))
                radioGroup.check(R.id.layersUsAreaBtn);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setMap();

            }
        });

        CheckBox enabledBox = (CheckBox) view.findViewById(R.id.layersUSEnabledBtn);
        enabledBox.setChecked(n.mapController.getChartBundleEnabled());

        enabledBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setMap();
            }
        });

        this.view = view;
        setMap();
        return view;
    }

    private void setMap()
    {
        CheckBox enabledBox = (CheckBox) view.findViewById(R.id.layersUSEnabledBtn);
        boolean enabled = enabledBox.isChecked();

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.usLayersGroup);
        RadioButton rb = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());
        TileProviderFormats.chartBundleLayer layer = TileProviderFormats.chartBundleLayer.sec_4326;

        if (rb.getTag().equals("sec_4326")) layer = TileProviderFormats.chartBundleLayer.sec_4326;
        if (rb.getTag().equals("wac_4326")) layer = TileProviderFormats.chartBundleLayer.wac_4326;
        if (rb.getTag().equals("tac_4326")) layer = TileProviderFormats.chartBundleLayer.tac_4326;
        if (rb.getTag().equals("enrh_4326")) layer = TileProviderFormats.chartBundleLayer.enrh_4326;
        if (rb.getTag().equals("enrl_4326")) layer = TileProviderFormats.chartBundleLayer.enrl_4326;
        if (rb.getTag().equals("enra_4326")) layer = TileProviderFormats.chartBundleLayer.enra_4326;

        n.mapController.ShowUSMap(layer, enabled);
    }

}

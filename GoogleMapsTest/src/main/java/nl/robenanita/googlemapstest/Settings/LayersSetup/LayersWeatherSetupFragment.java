package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Wms.TileProviderFormats;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LayersWeatherSetupFragment extends Fragment {

    NavigationActivity n;

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public LayersWeatherSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  (LinearLayout) inflater.inflate(R.layout.fragment_layers_weather_setup, container, false);

        n = (NavigationActivity)container.getContext();

        CheckBox cloudsBox = (CheckBox) view.findViewById(R.id.openWeatherCloudsChkBox);
        CheckBox pressBox = (CheckBox) view.findViewById(R.id.openWeatherPressChkBox);
        CheckBox precipBox = (CheckBox) view.findViewById(R.id.openWeatherPrecipChkBox);
        CheckBox windBox = (CheckBox) view.findViewById(R.id.canadaWeatherWindChkBox);
        CheckBox usRadarBox = (CheckBox) view.findViewById(R.id.canadaWeatherUsRadarChkBox);

        usRadarBox.setChecked(n.mapController.getVisibleWeatherChart(TileProviderFormats.weathermapLayer.RADAR_RDBR));
        windBox.setChecked(n.mapController.getVisibleWeatherChart(TileProviderFormats.weathermapLayer.ETA_UU));
        cloudsBox.setChecked(n.mapController.getVisibleWeatherChart(TileProviderFormats.weathermapLayer.clouds));
        precipBox.setChecked(n.mapController.getVisibleWeatherChart(TileProviderFormats.weathermapLayer.precipitation));
        pressBox.setChecked(n.mapController.getVisibleWeatherChart(TileProviderFormats.weathermapLayer.pressure_cntr));

        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.getText().equals("North American Radar"))
                    n.mapController.ShowWeatherMap(b, TileProviderFormats.weathermapLayer.RADAR_RDBR);
                if(compoundButton.getText().equals("Global Wind Arrows"))
                    n.mapController.ShowWeatherMap(b, TileProviderFormats.weathermapLayer.ETA_UU);
                if(compoundButton.getText().equals("Clouds"))
                    n.mapController.ShowWeatherMap(b, TileProviderFormats.weathermapLayer.clouds);
                if(compoundButton.getText().equals("Precipitation"))
                    n.mapController.ShowWeatherMap(b, TileProviderFormats.weathermapLayer.precipitation);
                if(compoundButton.getText().equals("Pressure Contours"))
                    n.mapController.ShowWeatherMap(b, TileProviderFormats.weathermapLayer.pressure_cntr);

                // Todo: Save settings to DB

            }
        };

        cloudsBox.setOnCheckedChangeListener(onCheckedChangeListener);
        pressBox.setOnCheckedChangeListener(onCheckedChangeListener);
        precipBox.setOnCheckedChangeListener(onCheckedChangeListener);
        windBox.setOnCheckedChangeListener(onCheckedChangeListener);
        usRadarBox.setOnCheckedChangeListener(onCheckedChangeListener);

        return view;
    }


}

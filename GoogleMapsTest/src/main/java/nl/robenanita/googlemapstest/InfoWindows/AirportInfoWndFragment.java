package nl.robenanita.googlemapstest.InfoWindows;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.FrequenciesDataSource;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;

/**
 * A simple {@link Fragment} subclass.
 */
public class AirportInfoWndFragment extends Fragment {


    public AirportInfoWndFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_airport_info_window, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        infoTxt = (TextView) view.findViewById(R.id.infoWindowInfoTxt);
        runwaysTxt = (TextView) view.findViewById(R.id.infoWindowRunwaysTxt);
        frequenciesTxt = (TextView) view.findViewById(R.id.infoWindowFrequenciesTxt);

        if (airport != null) setAirport();
    }

    private TextView infoTxt;
    private TextView runwaysTxt;
    private TextView frequenciesTxt;
    private Airport airport;


    private void setAirport()
    {
        String info = airport.getAirportInfoString();
        infoTxt.setText(info);

        info = airport.getRunwaysInfo();
        runwaysTxt.setText(info);

        info = airport.getFrequenciesInfo();
        frequenciesTxt.setText(info);
    }

    public void SetAirport(Airport airport, Context context)
    {
        this.airport = airport;
        if (this.airport != null)
        {
            RunwaysDataSource runwaysDataSource = new RunwaysDataSource(context);
            runwaysDataSource.open();
            this.airport.runways = runwaysDataSource.loadRunwaysByAirport(this.airport);
            runwaysDataSource.close();

            FrequenciesDataSource frequenciesDataSource = new FrequenciesDataSource(context);
            frequenciesDataSource.open();
            this.airport.frequencies = frequenciesDataSource.loadFrequenciesByAirport(this.airport);
            frequenciesDataSource.close();
        }
    }
}

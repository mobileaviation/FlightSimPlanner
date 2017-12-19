package nl.robenanita.googlemapstest.InfoWindows;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Weather.Metar;
import nl.robenanita.googlemapstest.Weather.MetarRawAdapter;
import nl.robenanita.googlemapstest.Weather.Notam;
import nl.robenanita.googlemapstest.Weather.NotamRawAdapter;
import nl.robenanita.googlemapstest.Weather.NotamsWebService;
import nl.robenanita.googlemapstest.Weather.Taf;
import nl.robenanita.googlemapstest.Weather.TafRawAdapter;
import nl.robenanita.googlemapstest.Weather.WeatherWebService;
import nl.robenanita.googlemapstest.database.AirportInfoDataSource;
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
        MetarBtn = (Button) view.findViewById(R.id.airportInfoMetarBtn);
        NotamBtn = (Button) view.findViewById(R.id.airportInfoNotamBtn);
        TafBtn = (Button) view.findViewById(R.id.airportInfoTafBtn);
        infoLayout = (FrameLayout) view.findViewById(R.id.airpotrtInfoNotamMetarTafLayout);
        listView = (ListView) view.findViewById(R.id.airportsInfoWindowListView);
        progressBar = (ProgressBar) view.findViewById(R.id.InfoWindowProgressBar);

        setButtonListeners();
        if (airport != null) setAirport();
    }

    private TextView infoTxt;
    private TextView runwaysTxt;
    private TextView frequenciesTxt;
    private Airport airport;
    private Button MetarBtn;
    private Button NotamBtn;
    private Button TafBtn;
    private FrameLayout infoLayout;
    private ListView listView;
    private ProgressBar progressBar;

    private Context context;

    private void setAirport()
    {
        String info = airport.getAirportInfoString();
        infoTxt.setText(info);

        info = airport.getRunwaysInfo();
        runwaysTxt.setText(info);

        info = airport.getFrequenciesInfo();
        frequenciesTxt.setText(info);
    }

    private void setButtonListeners()
    {
        MetarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoLayout();
                setMetars(airport.ident);
            }
        });

        NotamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoLayout();
                setNotams(airport.ident);
            }
        });

        TafBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoLayout();
                setTafs(airport.ident);
            }
        });

    }

    private void setInfoLayout() {
        if (infoLayout.getVisibility() == View.INVISIBLE) {
            infoLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else
        {
            infoLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void SetAirport(Airport airport, Context context)
    {
        this.airport = airport;
        this.context = context;
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

    private void setNotams(String code)
    {
        NotamsWebService s = new NotamsWebService(context);
        setNotamsWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        s.GetNotamsFromFAAByICAO(code);
    }

    private void setMetars(String code)
    {
        WeatherWebService s = new WeatherWebService(context);
        setWeatherWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        s.GetMetarsByICAO(icaos);
    }

    private void setTafs(String code)
    {
        WeatherWebService s = new WeatherWebService(context);
        setWeatherWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        s.GetTafsByICAO(icaos);
    }

    private void setWeatherWebServiceDataAvailableListener(WeatherWebService service)
    {
        service.setOnDataAvailable(new WeatherWebService.OnDataAvailable() {
            @Override
            public void OnMetarsAvailable(ArrayList<Metar> metars) {
                setupMetarsView(metars);
            }

            @Override
            public void OnTafsAvailable(ArrayList<Taf> tafs) {
                setupTafsView(tafs);
            }

        });
    }

    private void setNotamsWebServiceDataAvailableListener(NotamsWebService service)
    {
        service.setOnDataAvailable(new NotamsWebService.OnDataAvailable() {
            @Override
            public void OnNotamsAvailable(ArrayList<Notam> notams) {
                setupNotamsView(notams);
            }
        });
    }

    private void setupMetarsView(ArrayList<Metar> metars)
    {
        MetarRawAdapter adapter = new MetarRawAdapter(metars);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        if (metars.size()>0) {
            AirportInfoDataSource airportInfoDataSource = new AirportInfoDataSource(context);
            airportInfoDataSource.open();
            try {
                airportInfoDataSource.ResetMetars(metars.get(0));
                for (Metar metar : metars) {
                    metar.airport = airport;
                    airportInfoDataSource.InsertMetar(metar);
                }
            } finally {
                airportInfoDataSource.close();
            }

        }
    }

    private void setupTafsView(ArrayList<Taf> tafs)
    {
        TafRawAdapter adapter = new TafRawAdapter(tafs);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        if (tafs.size()>0) {
            AirportInfoDataSource airportInfoDataSource = new AirportInfoDataSource(context);
            airportInfoDataSource.open();
            try {
                airportInfoDataSource.ResetTafs(tafs.get(0));
                for (Taf taf : tafs) {
                    taf.airport = airport;
                    airportInfoDataSource.InsertTaf(taf);
                }
            } finally {
                airportInfoDataSource.close();
            }

        }
    }

    private void setupNotamsView(ArrayList<Notam> notams)
    {
        NotamRawAdapter adapter = new NotamRawAdapter(notams);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        if (notams.size()>0) {
            AirportInfoDataSource airportInfoDataSource = new AirportInfoDataSource(context);
            airportInfoDataSource.open();
            try {
                airportInfoDataSource.ResetNotams(notams.get(0));
                for (Notam notam : notams) {
                    notam.airport = airport;
                    airportInfoDataSource.InsertNotam(notam);
                }
            } finally {
                airportInfoDataSource.close();
            }

        }
    }
}

package nl.robenanita.googlemapstest;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Weather.Fir;
import nl.robenanita.googlemapstest.Weather.Metar;
import nl.robenanita.googlemapstest.Weather.MetarRawAdapter;
import nl.robenanita.googlemapstest.Weather.Notam;
import nl.robenanita.googlemapstest.Weather.NotamRawAdapter;
import nl.robenanita.googlemapstest.Weather.NotamsWebService;
import nl.robenanita.googlemapstest.Weather.Station;
import nl.robenanita.googlemapstest.Weather.StationsAdapter;
import nl.robenanita.googlemapstest.Weather.StationsWebService;
import nl.robenanita.googlemapstest.Weather.Taf;
import nl.robenanita.googlemapstest.Weather.TafRawAdapter;
import nl.robenanita.googlemapstest.Weather.Type;
import nl.robenanita.googlemapstest.Weather.WeatherWebService;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.AirportInfoDataSource;
import nl.robenanita.googlemapstest.database.FirDataSource;


public class AirportsInfoFragment extends Fragment {

    public AirportsInfoFragment() {
        // Required empty public constructor
    }

    private String TAG = "GooglemapsTest";

    private LinearLayout airportsInfoListLayout;
    private ListView infoListView;
    private ListView icaoCodesListView;
    private Button notamsBtn;
    private Button metarBtn;
    private Button tafBtn;
    private Button chartBtn;
    private TextView airportIdentText;
    private TextView infoTypeText;
    private TextView selectAirportForInfoText;
    private View view;
    private Integer infoListViewVisibility;
    private Type typeVisible;
    private ProgressBar infoProgressBar;
    private Airport airport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_airports_info, container, false);

        infoListView = (ListView)view.findViewById(R.id.airportsInfoListView);
        icaoCodesListView = (ListView)view.findViewById(R.id.airportsInfoIcaoListView);
        airportIdentText = (TextView) view.findViewById(R.id.airportIdentText);
        infoTypeText = (TextView) view.findViewById(R.id.infoTypeText);
        airportsInfoListLayout = (LinearLayout) view.findViewById(R.id.airportsInfoListLayout);
        infoProgressBar = (ProgressBar) view.findViewById(R.id.airportInfoProgressBar);
        selectAirportForInfoText = (TextView) view.findViewById(R.id.selectAirportForInfoText);

        infoListViewVisibility = airportsInfoListLayout.getVisibility();

        notamsBtn = (Button)view.findViewById(R.id.notamBtn);
        metarBtn = (Button)view.findViewById(R.id.metarBtn);
        tafBtn = (Button)view.findViewById(R.id.tafBtn);
        chartBtn = (Button)view.findViewById(R.id.chartBtn);

        notamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility(Type.vatme_notam);
                if (infoListViewVisibility==View.VISIBLE) {
                    typeVisible = Type.vatme_notam;
                    setStations();// setNotams();
                }
            }
        });

        metarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility(Type.metar);
                if (infoListViewVisibility==View.VISIBLE) {
                    typeVisible = Type.metar;
                    setStations();
                }// setMetars();
            }
        });

        tafBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility(Type.taf);
                if (infoListViewVisibility==View.VISIBLE) {
                    typeVisible = Type.taf;
                    setStations();
                } //setTafs();
            }
        });

        icaoCodesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StationsAdapter adapter = (StationsAdapter)adapterView.getAdapter();
                Log.i(TAG, "Code" + adapter.getStation(i).station_id);
                infoListView.setVisibility(View.GONE);
                infoProgressBar.setVisibility(View.VISIBLE);
                selectAirportForInfoText.setVisibility(View.GONE);
                icaoCodesListView.invalidateViews();
                adapter.setSelectedIndex(i);
                switch (typeVisible) {
                    case metar :
                        setMetars(adapter.getStation(i).station_id); break;
                    case taf:
                        setTafs(adapter.getStation(i).station_id); break;
                    case openaviation_notam:
                        break;
                    case vatme_notam:
                        setNotams(adapter.getStation(i).station_id); break;
                    case station:
                        break;
                }
            }
        });

        return view;
    }

    private void setNotams(String code)
    {
        NotamsWebService s = new NotamsWebService(view.getContext());
        setNotamsWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        infoTypeText.setText("Notams");

        s.GetNotamsFromFAAByICAO(code);

        //s.GetNotamsByICAOs(icaos);
        setAirport(code);
    }

    private void setMetars(String code)
    {
        WeatherWebService s = new WeatherWebService(view.getContext());
        setWeatherWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        infoTypeText.setText("Metar");
        s.GetMetarsByICAO(icaos);
        setAirport(code);
    }

    private void setTafs(String code)
    {
        WeatherWebService s = new WeatherWebService(view.getContext());
        setWeatherWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        infoTypeText.setText("Taf");
        s.GetTafsByICAO(icaos);
        setAirport(code);
    }

    private void setAirport(String code)
    {
        airport = getAirport(code);
        airportIdentText.setText(code);
    }

    private Airport getAirport(String code)
    {
        Airport airport;
        AirportDataSource airportDataSource = new AirportDataSource(view.getContext());
        airportDataSource.open(-1);
        airport = airportDataSource.GetAirportByIDENT(code);
        return airport;
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

    private void setStationsWebServiceDataAvailableListener(StationsWebService service)
    {
        service.setOnDataAvailable(new StationsWebService.OnDataAvailable() {
            @Override
            public void OnStationsAvailable(ArrayList<Station> stations) {
                setupStationsView(stations);
            }
        });
    }

    private void setStations()
    {
        //StationsWebService s = new StationsWebService();
        Geometry g2 = null;
        NavigationActivity activity = (NavigationActivity)getActivity();
        if (activity.selectedFlightplan != null)
        {
            g2 = activity.selectedFlightplan.buffer;
        }
        else
        {
            // Create  buffer around curposition
            Geometry g1 = new GeometryFactory().createPoint(new Coordinate(activity.curPosition.longitude,
                    activity.curPosition.latitude));
            BufferOp bufOp = new BufferOp(g1);
            bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
            g2 = bufOp.getResultGeometry(1);
        }

        AirportDataSource airportDataSource = new AirportDataSource(activity);
        airportDataSource.open(-1);
        ArrayList<Station> stations = airportDataSource.getAirportsInBuffer(g2);
        setupStationsView(stations);
        airportDataSource.close();

        //setStationsWebServiceDataAvailableListener(s);

        //s.GetStationsByLocationRadius(activity.curPosition, 80);


    }

    private void setInfoListViewVisibility(Type typeVisible)
    {
        //infoListViewVisibility = (infoListViewVisibility==view.GONE) ? view.VISIBLE : view.GONE;
        if (infoListViewVisibility==view.GONE) infoListViewVisibility = view.VISIBLE; else
            if ((infoListViewVisibility==view.VISIBLE) && (this.typeVisible!=typeVisible))
                infoListViewVisibility = view.VISIBLE; else infoListViewVisibility = view.GONE;
        airportsInfoListLayout.setVisibility(infoListViewVisibility);
        icaoCodesListView.setVisibility(infoListViewVisibility);
        selectAirportForInfoText.setVisibility(infoListViewVisibility);
        infoListView.setVisibility(view.GONE);
        if (typeVisible == Type.metar) infoTypeText.setText("Metar");
        if (typeVisible == Type.taf) infoTypeText.setText("Taf");
        if (typeVisible == Type.vatme_notam) infoTypeText.setText("Notams");
        airportIdentText.setText("....");
        infoListView.setAdapter(null);
        icaoCodesListView.setAdapter(null);
    }

    public void setupMetarsView(ArrayList<Metar> metars)
    {
        final ListView listView = (ListView) view.findViewById(R.id.airportsInfoListView);
        MetarRawAdapter adapter = new MetarRawAdapter(metars);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        infoProgressBar.setVisibility(View.GONE);

        if (metars.size()>0) {
            getAirport(metars.get(0).station_id);
            AirportInfoDataSource airportInfoDataSource = new AirportInfoDataSource(view.getContext());
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

    public void setupTafsView(ArrayList<Taf> tafs)
    {
        final ListView listView = (ListView) view.findViewById(R.id.airportsInfoListView);
        TafRawAdapter adapter = new TafRawAdapter(tafs);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        infoProgressBar.setVisibility(View.GONE);

        if (tafs.size()>0) {
            getAirport(tafs.get(0).station_id);
            AirportInfoDataSource airportInfoDataSource = new AirportInfoDataSource(view.getContext());
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

    public void setupNotamsView(ArrayList<Notam> notams)
    {
        final ListView listView = (ListView) view.findViewById(R.id.airportsInfoListView);
        NotamRawAdapter adapter = new NotamRawAdapter(notams);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        infoProgressBar.setVisibility(View.GONE);
        setListViewListeners();

        if (notams.size()>0) {
            getAirport(notams.get(0).getStation_id());
            AirportInfoDataSource airportInfoDataSource = new AirportInfoDataSource(view.getContext());
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

    private void setListViewListeners()
    {
        final ListView listView = (ListView) view.findViewById(R.id.airportsInfoListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavigationActivity activity = (NavigationActivity)getActivity();
                switch (typeVisible) {
                    case metar :
                        break;
                    case taf:
                        break;
                    case openaviation_notam:
                        break;
                    case vatme_notam:
                        NotamRawAdapter adapter1 = (NotamRawAdapter) adapterView.getAdapter();
                        Notam notam1 = adapter1.getNotam(i);
                        notam1.PlaceNotamMarker(activity.map);
                        break;
                    case faa_notam:
                        NotamRawAdapter adapter2 = (NotamRawAdapter) adapterView.getAdapter();
                        Notam notam2 = adapter2.getNotam(i);
                        notam2.PlaceNotamMarker(activity.map);
                        break;
                    case station:
                        break;
                }
            }
        });
    }

    public void setupStationsView(ArrayList<Station> stations)
    {
        Log.i(TAG,"Stations setup");
        FirDataSource firDataSource = new FirDataSource(view.getContext());

        ArrayList<Fir> firs = null;
        try {
            firDataSource.open();
            firs = firDataSource.GetPossibleFirs(stations);
        } finally {
            firDataSource.close();
        }

        if (firs != null)
        {
            for (Fir fir : firs)
            {
                Station station = new Station();
                station.fir = true;
                station.station_id = fir.ident;
                stations.add(0, station);
            }
        }

        StationsAdapter adapter = new StationsAdapter(stations);
        icaoCodesListView.setAdapter(adapter);
    }

}

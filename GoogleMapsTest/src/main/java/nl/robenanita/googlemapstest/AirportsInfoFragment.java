package nl.robenanita.googlemapstest;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.util.ArrayList;

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


public class AirportsInfoFragment extends Fragment {

    public AirportsInfoFragment() {
        // Required empty public constructor
    }

    private String TAG = "GooglemapsTest";

    private ListView infoListView;
    private ListView icaoCodesListView;
    private Button notamsBtn;
    private Button metarBtn;
    private Button tafBtn;
    private Button chartBtn;
    private View view;
    private Integer infoListViewVisibility;
    private Type typeVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_airports_info, container, false);

        infoListView = (ListView)view.findViewById(R.id.airportsInfoListView);
        icaoCodesListView = (ListView)view.findViewById(R.id.airportsInfoIcaoListView);

        infoListViewVisibility = infoListView.getVisibility();

        notamsBtn = (Button)view.findViewById(R.id.notamBtn);
        metarBtn = (Button)view.findViewById(R.id.metarBtn);
        tafBtn = (Button)view.findViewById(R.id.tafBtn);
        chartBtn = (Button)view.findViewById(R.id.chartBtn);

        notamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility();
                if (infoListViewVisibility==View.VISIBLE) {
                    typeVisible = Type.vatme_notam;
                    setStations();// setNotams();
                }
            }
        });

        metarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility();
                if (infoListViewVisibility==View.VISIBLE) {
                    typeVisible = Type.metar;
                    setStations();
                }// setMetars();
            }
        });

        tafBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility();
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
        NotamsWebService s = new NotamsWebService();
        setNotamsWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        s.GetNotamsByICAOs(icaos);
    }

    private void setMetars(String code)
    {
        WeatherWebService s = new WeatherWebService();
        setWeatherWebServiceDataAvailableListener(s);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add(code);
        s.GetMetarsByICAO(icaos);
    }

    private void setTafs(String code)
    {
        WeatherWebService s = new WeatherWebService();
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
        setupStationsView(airportDataSource.getAirportsInBuffer(g2));
        airportDataSource.close();

        //setStationsWebServiceDataAvailableListener(s);

        //s.GetStationsByLocationRadius(activity.curPosition, 80);


    }

    private void setInfoListViewVisibility()
    {
        infoListViewVisibility = (infoListViewVisibility==view.GONE) ? view.VISIBLE : view.GONE;
        infoListView.setVisibility(infoListViewVisibility);
        icaoCodesListView.setVisibility(infoListViewVisibility);
        infoListView.setAdapter(null);
        icaoCodesListView.setAdapter(null);
    }

    public void setupMetarsView(ArrayList<Metar> metars)
    {
        final ListView listView = (ListView) view.findViewById(R.id.airportsInfoListView);
        MetarRawAdapter adapter = new MetarRawAdapter(metars);
        listView.setAdapter(adapter);
    }

    public void setupTafsView(ArrayList<Taf> tafs)
    {
        final ListView listView = (ListView) view.findViewById(R.id.airportsInfoListView);
        TafRawAdapter adapter = new TafRawAdapter(tafs);
        listView.setAdapter(adapter);
    }

    public void setupNotamsView(ArrayList<Notam> notams)
    {
        final ListView listView = (ListView) view.findViewById(R.id.airportsInfoListView);
        NotamRawAdapter adapter = new NotamRawAdapter(notams);
        listView.setAdapter(adapter);
    }

    public void setupStationsView(ArrayList<Station> stations)
    {
        Log.i(TAG,"Stations setup");
        StationsAdapter adapter = new StationsAdapter(stations);
        icaoCodesListView.setAdapter(adapter);
    }

}

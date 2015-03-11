package nl.robenanita.googlemapstest;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Weather.Metar;
import nl.robenanita.googlemapstest.Weather.MetarRawAdapter;
import nl.robenanita.googlemapstest.Weather.Notam;
import nl.robenanita.googlemapstest.Weather.NotamRawAdapter;
import nl.robenanita.googlemapstest.Weather.StationsAdapter;
import nl.robenanita.googlemapstest.Weather.Taf;
import nl.robenanita.googlemapstest.Weather.TafRawAdapter;
import nl.robenanita.googlemapstest.Weather.WeatherWebService;



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
                if (infoListViewVisibility==View.VISIBLE) setNotams();
            }
        });

        metarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility();
                if (infoListViewVisibility==View.VISIBLE) setStations();// setMetars();
            }
        });

        tafBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfoListViewVisibility();
                if (infoListViewVisibility==View.VISIBLE) setStations(); //setTafs();
            }
        });

        return view;
    }

    private void setNotams()
    {
        WeatherWebService s = new WeatherWebService(AirportsInfoFragment.this);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add("EHLE");
        icaos.add("EHAM");
        icaos.add("EHTE");
        s.GetNotamsByICAOs(icaos);
    }

    private void setMetars()
    {
        WeatherWebService s = new WeatherWebService(AirportsInfoFragment.this);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add("EHLE");
        icaos.add("EHAM");
        icaos.add("EHTE");
        s.GetMetarsByICAO(icaos);
    }

    private void setTafs()
    {
        WeatherWebService s = new WeatherWebService(AirportsInfoFragment.this);
        ArrayList<String> icaos = new ArrayList<String>();
        icaos.add("EHLE");
        icaos.add("EHAM");
        icaos.add("EHTE");
        s.GetTafsByICAO(icaos);
    }

    private void setStations()
    {
        WeatherWebService s = new WeatherWebService(AirportsInfoFragment.this);
        NavigationActivity activity = (NavigationActivity)getActivity();

        s.GetStationsByLocationRadius(activity.curPosition, 100);
    }

    private void setInfoListViewVisibility()
    {
        infoListViewVisibility = (infoListViewVisibility==view.GONE) ? view.VISIBLE : view.GONE;
        infoListView.setVisibility(infoListViewVisibility);
        icaoCodesListView.setVisibility(infoListViewVisibility);
        Log.i(TAG, "Info List Visiblity: " + infoListViewVisibility.toString());
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

    public void setupStationsView(ArrayList<String> stations)
    {
        Log.i(TAG,"Stations setup");
        StationsAdapter adapter = new StationsAdapter(stations);
        icaoCodesListView.setAdapter(adapter);
    }

}

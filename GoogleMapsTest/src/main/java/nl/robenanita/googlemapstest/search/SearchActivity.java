package nl.robenanita.googlemapstest.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Map;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.AirportAdapter;
import nl.robenanita.googlemapstest.Fix;
import nl.robenanita.googlemapstest.FixesAdapter;
import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.NavaidAdapter;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.AirportDataSource;
import nl.robenanita.googlemapstest.database.FixesDataSource;
import nl.robenanita.googlemapstest.database.MarkerProperties;
import nl.robenanita.googlemapstest.database.NavaidsDataSource;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;

public class SearchActivity extends ActionBarActivity {

    private ListView airportListView;
    private ListView navaidsListView;
    private ListView fixesListView;

    private LatLngBounds initialSearchBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //loadAds();

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setup();

        TabSpec airportTab = tabHost.newTabSpec("Airports Tab");
        TabSpec navaidTab = tabHost.newTabSpec("Navaids Tab");
        TabSpec fixTab = tabHost.newTabSpec("Fixes Tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        airportTab.setIndicator("Airports");
        airportTab.setContent(R.id.airportSearchLayout);
//
        navaidTab.setIndicator("Navaids");
        navaidTab.setContent(R.id.navaidsSearchLayout);
//
        fixTab.setIndicator("Fixes");
        fixTab.setContent(R.id.fixesSearchLayout);

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(airportTab);
        tabHost.addTab(navaidTab);
        tabHost.addTab(fixTab);

        setupListViewSelectListeners();

        SearchAirports();
        SearchNavaids();
        SearchFixes();

        setupInitialSearch();
    }

    private void setupInitialSearch()
    {
        Intent intent = getIntent();
        Bundle b = intent.getParcelableExtra("location");
        LatLng location = b.getParcelable("location");

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open();
        MarkerProperties markerProperties = propertiesDataSource.getMarkersProperties();
        propertiesDataSource.close();

        double distance = 50000;

        double latspan = (distance/111325);
        double longspan = (distance/111325)*(1/ Math.cos(Math.toRadians(location.latitude)));

        LatLngBounds bounds = new LatLngBounds(
                new LatLng(location.latitude-latspan, location.longitude-longspan),
                new LatLng(location.latitude+latspan, location.longitude+longspan));

        AirportDataSource airportDataSource = new AirportDataSource(getBaseContext());
        airportDataSource.open(-1);
        Map<Integer, Airport> airportMap = airportDataSource.getAirportsByCoordinateAndZoomLevel(bounds, 10f, null, markerProperties);
        airportDataSource.close();
        NavaidsDataSource navaidsDataSource = new NavaidsDataSource(getBaseContext());
        navaidsDataSource.open(-1);
        Map<Integer, Navaid> navaidMap = navaidsDataSource.GetNaviadsByCoordinateAndZoomLevel(bounds, 8f, null);
        navaidsDataSource.close();
        FixesDataSource fixesDataSource = new FixesDataSource(getBaseContext());
        fixesDataSource.open(-1);
        Map<Integer, Fix> fixesMap = fixesDataSource.getFixesByCoordinateAndZoomLevel(bounds, 8f, null);
        fixesDataSource.close();


        setupFixesList(fixesMap);
        setupNavaidsList(navaidMap);
        setupAirportsList(airportMap);
    }

    private void setupListViewSelectListeners()
    {
        airportListView = (ListView) findViewById(R.id.searchAirportsListView);
        navaidsListView = (ListView) findViewById(R.id.searchNavaidsListView);
        fixesListView = (ListView) findViewById(R.id.searchFixesListView);

        airportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                airportListView.setSelection(i);
                AirportAdapter a = (AirportAdapter) adapterView.getAdapter();
                Airport airport = a.GetAirport(i);
                Intent intent=new Intent();
                intent.putExtra("Type","airport");
                intent.putExtra("id", airport.id);
                setResult(101, intent);
                finish();//finishing activit 

            }
        });

        navaidsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                navaidsListView.setSelection(i);
                NavaidAdapter a = (NavaidAdapter) adapterView.getAdapter();
                Navaid navaid = a.GetNavaid(i);
                Intent intent=new Intent();
                intent.putExtra("Type","navaid");
                intent.putExtra("id", navaid.id);
                setResult(102,intent);
                finish();//finishing activity
            }

        });

        fixesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fixesListView.setSelection(i);
                FixesAdapter a = (FixesAdapter) adapterView.getAdapter();
                Fix fix = a.GetFix(i);
                Intent intent=new Intent();
                intent.putExtra("Type","fix");
                intent.putExtra("id", fix.id);
                setResult(103,intent);
                finish();//finishing activity
            }
        });
    }

    private void SearchAirports()
    {
        EditText searchAirportsTxt = (EditText) findViewById(R.id.searchActivityAirportText);
        searchAirportsTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                AirportDataSource airportData = new AirportDataSource(getBaseContext());

                airportData.open(-1);
                Map<Integer, Airport> airportMap = airportData.SearchAirportNameCode(charSequence.toString());
                airportData.close();

                setupAirportsList(airportMap);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupAirportsList(Map<Integer, Airport> airportMap)
    {
        //airportListView = (ListView) findViewById(R.id.searchAirportsListView);
        AirportAdapter adapter = new AirportAdapter(airportMap);
        airportListView.setAdapter(adapter);
    }


    private void SearchNavaids()
    {
        EditText searchNavaidsTxt = (EditText) findViewById(R.id.searchActivityNavaidsTxt);
        searchNavaidsTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                NavaidsDataSource navaidsData = new NavaidsDataSource(getBaseContext());

                navaidsData.open(-1);
                Map<Integer, Navaid> navaidsMap = navaidsData.SearchNavaidsByNameOrCode(charSequence.toString());
                navaidsData.close();

                setupNavaidsList(navaidsMap);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupNavaidsList(Map<Integer, Navaid> navaidsMap)
    {
        //navaidsListView = (ListView) findViewById(R.id.searchNavaidsListView);
        NavaidAdapter adapter = new NavaidAdapter(navaidsMap);
        navaidsListView.setAdapter(adapter);
    }

    private void SearchFixes()
    {
        EditText searchFixesTxt = (EditText) findViewById(R.id.searchActivityFixesTxt);
        searchFixesTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                FixesDataSource fixesData = new FixesDataSource(getBaseContext());

                fixesData.open(-1);
                Map<Integer, Fix> fixesMap = fixesData.SearchFix(charSequence.toString());
                fixesData.close();

                setupFixesList(fixesMap);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupFixesList(Map<Integer, Fix> fixesMap)
    {
        //fixesListView = (ListView) findViewById(R.id.searchFixesListView);
        FixesAdapter adapter = new FixesAdapter(fixesMap);
        fixesListView.setAdapter(adapter);
    }

//    public void loadAds() {
//        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
//        propertiesDataSource.open();
//        boolean ads = propertiesDataSource.checkNoAdvertisements();
//        propertiesDataSource.close();
//
//        if (!ads) {
//            AdView MainAd = (AdView) findViewById(R.id.adSearch);
//            AdRequest r = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                    .addTestDevice("36A5D52DC49CF3A84F6BD03381312CE1")
//                    .addTestDevice("70F82DF4BD716E85F87B34C81B78C5ED")
//                    .build();
//            MainAd.loadAd(r);
//        }
//        else
//        {
//            LinearLayout adsLayout = (LinearLayout)findViewById(R.id.adsSearchLayout);
//            adsLayout.setVisibility(View.GONE);
//        }
//    }
}

package nl.robenanita.googlemapstest.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TabHost;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Map;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Airport.AirportAdapter;
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

/**
 * Created by Rob Verhoef on 12-5-2014.
 */
public class SearchPopup extends PopupWindow {
    private String TAG = "GooglemapsTest";
    private Context c;
    private View layout;

    private ListView airportListView;
    private ListView navaidsListView;
    private ListView fixesListView;

    public boolean Result;
    public Airport airport;
    public Navaid navaid;
    public Fix fix;

    public SearchPopup(Context context, View layout) {
        super(context);
        newSearchPopup(context, layout);
    }

    public SearchPopup(Context context, View layout, LatLng location)
    {
        super(context);
        newSearchPopup(context, layout);
        setupInitialSearch(location);
    }

    private void setupInitialSearch(LatLng location)
    {
        double distance = 50000;

        double latspan = (distance/111325);
        double longspan = (distance/111325)*(1/ Math.cos(Math.toRadians(location.latitude)));

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(layout.getContext());
        propertiesDataSource.open(true);
        MarkerProperties markerProperties = propertiesDataSource.getMarkersProperties();
        propertiesDataSource.close(true);

        LatLngBounds bounds = new LatLngBounds(
                new LatLng(location.latitude-latspan, location.longitude-longspan),
                new LatLng(location.latitude+latspan, location.longitude+longspan));

        AirportDataSource airportDataSource = new AirportDataSource(layout.getContext());
        airportDataSource.open(-1);
        Map<Integer, Airport> airportMap = airportDataSource.getAirportsByCoordinateAndZoomLevel(bounds, 8f, null, markerProperties);
        airportDataSource.close();
        NavaidsDataSource navaidsDataSource = new NavaidsDataSource(layout.getContext());
        navaidsDataSource.open(-1);
        Map<Integer, Navaid> navaidMap = navaidsDataSource.GetNaviadsByCoordinateAndZoomLevel(bounds, 8f, null);
        navaidsDataSource.close();
        FixesDataSource fixesDataSource = new FixesDataSource(layout.getContext());
        fixesDataSource.open(-1);
        Map<Integer, Fix> fixesMap = fixesDataSource.getFixesByCoordinateAndZoomLevel(bounds, 8f, null);
        fixesDataSource.close();


        setupFixesList(fixesMap);
        setupNavaidsList(navaidMap);
        setupAirportsList(airportMap);
    }

    private void newSearchPopup(Context context, View layout)
    {
        c = context;
        this.layout = layout;
        Result = false;

        TabHost tabHost = (TabHost) layout.findViewById(R.id.searchPopuptabhost);
        tabHost.setup();

        TabHost.TabSpec airportTab = tabHost.newTabSpec("Airports Tab");
        TabHost.TabSpec navaidTab = tabHost.newTabSpec("Navaids Tab");
        TabHost.TabSpec fixTab = tabHost.newTabSpec("Fixes Tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        airportTab.setIndicator("Airports");
        airportTab.setContent(R.id.airportSearchPopupLayout);
//
        navaidTab.setIndicator("Navaids");
        navaidTab.setContent(R.id.navaidsSearchPopupLayout);
//
        fixTab.setIndicator("Fixes");
        fixTab.setContent(R.id.fixesSearchPopupLayout);

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(airportTab);
        tabHost.addTab(navaidTab);
        tabHost.addTab(fixTab);

        setupListViewSelectListeners();

        SearchAirports();
        SearchNavaids();
        SearchFixes();
    }

    private void setupListViewSelectListeners()
    {
        airportListView = (ListView) layout.findViewById(R.id.searchPopupAirportsListView);
        navaidsListView = (ListView) layout.findViewById(R.id.searchPopupNavaidsListView);
        fixesListView = (ListView) layout.findViewById(R.id.searchPopupFixesListView);

        airportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                airportListView.setSelection(i);
                AirportAdapter a = (AirportAdapter) adapterView.getAdapter();
                airport = a.GetAirport(i);
                navaid = null;
                fix = null;
                Result = true;
                dismiss();
            }
        });

        navaidsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                navaidsListView.setSelection(i);
                NavaidAdapter a = (NavaidAdapter) adapterView.getAdapter();
                navaid = a.GetNavaid(i);
                airport = null;
                fix = null;
                Result = true;
                dismiss();
            }

        });

        fixesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fixesListView.setSelection(i);
                FixesAdapter a = (FixesAdapter) adapterView.getAdapter();
                fix = a.GetFix(i);
                airport = null;
                navaid = null;
                Result = true;
                dismiss();
            }
        });
    }

    private void SearchAirports()
    {
        EditText searchAirportsTxt = (EditText) layout.findViewById(R.id.searchPopupAirportText);
        searchAirportsTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                AirportDataSource airportData = new AirportDataSource(c);

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
        EditText searchNavaidsTxt = (EditText) layout.findViewById(R.id.searchPopupNavaidsTxt);
        searchNavaidsTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                NavaidsDataSource navaidsData = new NavaidsDataSource(c);

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
        EditText searchFixesTxt = (EditText) layout.findViewById(R.id.searchPopupFixesTxt);
        searchFixesTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                FixesDataSource fixesData = new FixesDataSource(c);

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
}

package nl.robenanita.googlemapstest.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.Map;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.AirportAdapter;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.AirportDataSource;

/**
 * Created by Rob Verhoef on 1-3-14.
 */
public class SearchAirportsPopup extends PopupWindow {
    private String TAG = "GooglemapsTest";
    private Context c;
    private View layout;
    private ListView airportListView;
    public Boolean Result;

    public Airport SelectedAirport;

    public SearchAirportsPopup(Context context, View layout)
    {
        super(context);
        c = context;
        this.layout = layout;
        Result = false;

        EditText searchText = (EditText) layout.findViewById(R.id.searchAirportText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.i(TAG, "Text Changed: " + charSequence.toString());
                AirportDataSource airportData = new AirportDataSource(c);

                airportData.open(-1);
                Map<Integer, Airport> airportMap = airportData.SearchAirportNameCode(charSequence.toString());
                airportData.close();

                //Log.i(TAG, "Found airports: " + airportMap.size());

                SetupListbox(airportMap);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        airportListView = (ListView) layout.findViewById(R.id.airportsListView);
        airportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                airportListView.setSelection(i);
                AirportAdapter a = (AirportAdapter) adapterView.getAdapter();
                Airport airport = a.GetAirport(i);
                Log.i(TAG, "Selected Airport: " + airport.name);
                SelectedAirport = airport;
                Result = true;
                dismiss();
            }
        });

//        airportListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.i(TAG, "ItemSelected: " + adapterView.toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Log.i(TAG, "onNothingSelected: " + adapterView.toString());
//            }
//        });
    }

    private void SetupListbox(Map<Integer, Airport> airportMap)
    {
        airportListView = (ListView) this.layout.findViewById(R.id.airportsListView);
        AirportAdapter adapter = new AirportAdapter(airportMap);
        airportListView.setAdapter(adapter);

//        airportListView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try
//                {
//                Log.i(TAG, "ItemSelected: " + airportListView.getSelectedItem().toString());
//                }
//                catch (Exception ee)
//                {
//
//                }
//            }
//        });
    }
}

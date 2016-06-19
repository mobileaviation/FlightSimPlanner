package nl.robenanita.googlemapstest;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import nl.robenanita.googlemapstest.search.SearchAirportsPopup;

public class FlightPlanActivity extends ActionBarActivity {
    private String TAG = "GooglemapsTest";

    private Button searchDepartureAirportBtn;
    private Button searchDestinationAirportBtn;
    private Button searchAlternateAirportBtn;

    private Airport departureAirport;
    private Airport destinationAirport;
    private Airport alternateAirport;

    private int airportType = 0;
    // 1 = departure
    // 2 = destination
    // 3 = alternate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_plan);

        searchAlternateAirportBtn = (Button) findViewById(R.id.searchAlternateAirportBtn);
        searchAlternateAirportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airportType = 3;
                ShowSearchAirportPopup();
            }
        });

        searchDepartureAirportBtn = (Button) findViewById(R.id.searchDepartureAirportBtn);
        searchDepartureAirportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airportType = 1;
                ShowSearchAirportPopup();
            }
        });

        searchDestinationAirportBtn = (Button) findViewById(R.id.searchDestinationAirportBtn);
        searchDestinationAirportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                airportType = 2;
                ShowSearchAirportPopup();
            }
        });

    }

    SearchAirportsPopup searchAirportsPopup;
    private void ShowSearchAirportPopup()
    {
        int popupWidth = 800;
        int popupHeight = 480;

        LinearLayout viewGroup = null;
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.searchairports_popup, viewGroup);

        searchAirportsPopup = new SearchAirportsPopup(this, Layout);

        searchAirportsPopup.setContentView(Layout);
        searchAirportsPopup.setWidth(popupWidth);
        searchAirportsPopup.setHeight(popupHeight);
        searchAirportsPopup.setFocusable(true);

        searchAirportsPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (searchAirportsPopup.SelectedAirport != null) {
                    switch (airportType) {
                        case 3: {
                            alternateAirport = searchAirportsPopup.SelectedAirport;
                            EditText alternateText = (EditText) findViewById(R.id.alternateAirportEdit);
                            alternateText.setText(alternateAirport.name);
                            Log.i(TAG, "Selected Alternate airport" + alternateAirport.name);
                            break;
                        }
                        case 2: {
                            destinationAirport = searchAirportsPopup.SelectedAirport;
                            EditText destinationEdit = (EditText) findViewById(R.id.destinationAirportEdit);
                            destinationEdit.setText(destinationAirport.name);
                            Log.i(TAG, "Selected Destination airport" + destinationAirport.name);
                            break;
                        }
                        case 1: {
                            departureAirport = searchAirportsPopup.SelectedAirport;
                            EditText departureEdit = (EditText) findViewById(R.id.departureAirportEdit);
                            departureEdit.setText(departureAirport.name);
                            Log.i(TAG, "Selected Departure airport" + departureAirport.name);
                            break;
                        }
                    }
                }
            }
        });

        searchAirportsPopup.showAtLocation(Layout, Gravity.CENTER, 0, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.flight_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

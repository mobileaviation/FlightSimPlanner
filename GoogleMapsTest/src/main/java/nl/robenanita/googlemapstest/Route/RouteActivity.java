package nl.robenanita.googlemapstest.Route;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Date;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.RouteDataSource;
import nl.robenanita.googlemapstest.search.SearchAirportsPopup;

public class RouteActivity extends ActionBarActivity {
    private String TAG = "GooglemapsTest";

    private Button searchDepartureAirportBtn;
    private Button searchDestinationAirportBtn;
    private Button searchAlternateAirportBtn;
    private Button saveBtn;
    private Button cancelBtn;

    private Airport departureAirport;
    private Airport destinationAirport;
    private Airport alternateAirport;

    private int airportType = 0;
    // 1 = departure
    // 2 = destination
    // 3 = alternate

    private EditText flightPlanName;
    private EditText flightPlanAltitude;
    private EditText flightPlanAirspeed;
    private EditText flightPlanWindspeed;
    private EditText flightPlanDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        //loadAds();

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

        flightPlanName = (EditText) findViewById(R.id.nameFlightPlanEdit);
        flightPlanAltitude = (EditText) findViewById(R.id.altitudeFlightPlanEdit);
        flightPlanAirspeed = (EditText) findViewById(R.id.airspeedFlightPlanEdit);

        saveBtn = (Button) findViewById(R.id.savePlanBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Route flightPlan = new Route(RouteActivity.this);

                flightPlan.departure_airport = departureAirport;
                flightPlan.destination_airport = destinationAirport;
                flightPlan.alternate_airport = alternateAirport;

                Boolean entryErtror = false;
                String errorMessage = "";


                if (flightPlanAltitude.getText().toString().equals(""))
                {
                    entryErtror = true;
                    errorMessage = "Please supply Altitude!";
                }
                else
                    flightPlan.altitude = Helpers.parseIntWithDefault(flightPlanAltitude.getText().toString(),1000);

                if (flightPlanAirspeed.getText().toString().equals(""))
                {
                    entryErtror = true;
                    errorMessage = "Please supply Airspeed!";
                }
                else
                    flightPlan.indicated_airspeed =   Helpers.parseIntWithDefault(flightPlanAirspeed.getText().toString(),100);

                if (flightPlanName.getText().toString().equals(""))
                {
                    entryErtror = true;
                    errorMessage = "Please supply Flightplan name!";
                }
                    flightPlan.name = flightPlanName.getText().toString();

                flightPlan.date = new Date();
                flightPlan.wind_direction = 0f;
                flightPlan.wind_speed = 0;

                Waypoint startWaypoint = new Waypoint();
                if (departureAirport!=null)
                {
                    startWaypoint.airport_id = departureAirport.id;
                    startWaypoint.name = departureAirport.name;
                    Location depLoc = new Location("loc dep");
                    depLoc.setLatitude(departureAirport.latitude_deg);
                    depLoc.setLongitude(departureAirport.longitude_deg);
                    startWaypoint.location = depLoc;
                    startWaypoint.order = 1;
                } else
                {
                    entryErtror = true;
                    errorMessage = "Please select a departure airport!";
                }

                Waypoint endWaypoint = new Waypoint();
                if (destinationAirport!=null)
                {
                    endWaypoint.airport_id = destinationAirport.id;
                    Location destLoc = new Location("loc dest");
                    endWaypoint.name = destinationAirport.name;
                    destLoc.setLatitude(destinationAirport.latitude_deg);
                    destLoc.setLongitude(destinationAirport.longitude_deg);
                    endWaypoint.location = destLoc;
                    endWaypoint.order = 1000;
                } else
                {
                    entryErtror = true;
                    errorMessage = "Please select a destination airport!";
                }

                if (alternateAirport==null)
                {
                    entryErtror = true;
                    errorMessage = "Please select an alternate airport!";
                }

                if(entryErtror)
                {
                    ShowAlertDialog("Flightplan Error", errorMessage);
                }
                else
                {
//                    endWaypoint.true_heading = startWaypoint.location.bearingTo(endWaypoint.location);
//                    if (endWaypoint.true_track<0) endWaypoint.true_track = endWaypoint.true_track + 360f;
//
//                    endWaypoint.distance_total = Math.round(startWaypoint.location.distanceTo(endWaypoint.location));

                    flightPlan.Waypoints.add(startWaypoint);
                    flightPlan.Waypoints.add(endWaypoint);
                    flightPlan.UpdateWaypointsData();

                    RouteDataSource flightPlanDataSource = new RouteDataSource(RouteActivity.this);
                    flightPlanDataSource.open();
                    flightPlanDataSource.InsertnewFlightPlan(flightPlan);
                    flightPlanDataSource.close();

                    finish();
                }
            }
        });

        cancelBtn = (Button) findViewById(R.id.cancelPlanBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void ShowAlertDialog(String Title, String Message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle(Title);

        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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

//    public void loadAds() {
//        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
//        propertiesDataSource.open();
//        boolean ads = propertiesDataSource.checkNoAdvertisements();
//        propertiesDataSource.close();
//
//        if (!ads) {
//            AdView MainAd = (AdView) findViewById(R.id.adCreateFlightplan);
//            AdRequest r = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                    .addTestDevice("36A5D52DC49CF3A84F6BD03381312CE1")
//                    .addTestDevice("70F82DF4BD716E85F87B34C81B78C5ED")
//                    .build();
//            MainAd.loadAd(r);
//        }
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.flight_plan, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}

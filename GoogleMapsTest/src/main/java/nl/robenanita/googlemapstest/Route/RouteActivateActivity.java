package nl.robenanita.googlemapstest.Route;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.RouteAdapter;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.RouteDataSource;

public class RouteActivateActivity extends ActionBarActivity {
    public Route selectedFlightplan;
    private ListView flightplanList;
    private String TAG = "GooglemapsTest";

    private EditText windDirEdit;
    private EditText windSpeedEdit;
    private Button activateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_plan_activate);

        selectedFlightplan = null;

        LoadFlightplans();
        SetupButtons();
    }

    private void LoadFlightplans()
    {
        RouteDataSource flightPlanDataSource = new RouteDataSource(this);
        flightPlanDataSource.open();
        ArrayList<Route> flightPlans = flightPlanDataSource.GetAllFlightplans();
        flightPlanDataSource.close();

        flightplanList = (ListView) findViewById(R.id.flightplanList);
        final RouteAdapter adapter = new RouteAdapter(flightPlans, this);
        flightplanList.setAdapter(adapter);

//        flightplanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                if (selectedFlightplan != null)
////                {
////                    if (selectedFlightplan.track != null)
////                        selectedFlightplan.track.remove();
////                    selectedFlightplan = null;
////                }
//
//                flightplanList.setSelection(i);
//                RouteAdapter adapter1 = (RouteAdapter) adapterView.getAdapter();
//                selectedFlightplan = adapter1.GetFlightplan(i);
//                Log.i(TAG, "Selected flightplan: " + selectedFlightplan.name);
//
//                adapter1.setSelectedIndex(i);
//                adapter1.notifyDataSetChanged();
//
//                windSpeedEdit.setText(selectedFlightplan.wind_speed.toString());
//                windDirEdit.setText(Float.toString(selectedFlightplan.wind_direction));
//                activateBtn.setEnabled(true);
//            }
//        });
        adapter.SetOnFlightplanClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer f = (Integer)view.getTag();
                adapter.setSelectedIndex(f);
                adapter.notifyDataSetChanged();

                selectedFlightplan = adapter.GetFlightplan(f);
                Log.i(TAG, "Selected Flightplan: " + selectedFlightplan.name);

                windSpeedEdit.setText(selectedFlightplan.wind_speed.toString());
                windDirEdit.setText(Float.toString(selectedFlightplan.wind_direction));
                activateBtn.setEnabled(true);
            }
        });
    }

    private void SetupButtons()
    {
        activateBtn = (Button) findViewById(R.id.activateFpActivateBtn);
        activateBtn.setEnabled(false);
        Button cancelBtn = (Button) findViewById(R.id.activateFpCancelBtn);

        windDirEdit = (EditText) findViewById(R.id.activateFpWindDirEdit);
        windSpeedEdit = (EditText) findViewById(R.id.activateFpWindSpeedEdit);

        activateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFlightplan != null) {
                    Intent intent = new Intent();
                    intent.putExtra("id", selectedFlightplan.id);



                    if (storeWind()) {
                        setResult(301, intent);
                        finish();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(302);
                finish();
            }
        });
    }

    private boolean storeWind()
    {
        boolean ret = true;
        String message = "";
        if (selectedFlightplan != null)
        {
            if (windDirEdit.getText().equals(""))
            {
                ret = false;
                message = "Please supply wind direction!";
                ShowAlertDialog(message, "Error");
            }
            else
                selectedFlightplan.wind_direction = Float.parseFloat(windDirEdit.getText().toString());

            if (windSpeedEdit.getText().equals(""))
            {
                ret = false;
                message = "Please supply wind speed!";
                ShowAlertDialog(message, "Error");
            }
            else
                selectedFlightplan.wind_speed = Integer.parseInt(windSpeedEdit.getText().toString());
        }

        if (ret)
        {
            //store the wind in the database...
            RouteDataSource flightPlanDataSource = new RouteDataSource(this);
            flightPlanDataSource.open();
            flightPlanDataSource.UpdateFlightplanWind(selectedFlightplan);
            flightPlanDataSource.close();
        }

        return ret;
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

}

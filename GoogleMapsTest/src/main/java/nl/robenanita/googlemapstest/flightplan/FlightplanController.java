package nl.robenanita.googlemapstest.flightplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Date;

import nl.robenanita.googlemapstest.HeadingError;
import nl.robenanita.googlemapstest.LegInfoView;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.VariationDeviationPopup;
import nl.robenanita.googlemapstest.database.FlightPlanDataSource;

/**
 * Created by Rob Verhoef on 31-7-2017.
 */

public class FlightplanController {
    public FlightplanController(Activity activity, FlightPlan flightPlan)
    {
        this.activity = activity;
        this.flightPlan = flightPlan;
    }

    private Activity activity;
    private FlightPlan flightPlan;
    private final String TAG = "FlightplanController";

    private OnFlightplanEvent onFlightplanEvent;
    public void SetOnFlightplanEvent(OnFlightplanEvent onFlightplanEvent)
    { this.onFlightplanEvent = onFlightplanEvent; }

    public void SetVariation(final Waypoint waypoint)
    {
        Log.i(TAG, "Variation Button clicked: " + waypoint.name);
        int popupWidth = 200;
        int popupHeight = 300;

        LinearLayout viewGroup = (LinearLayout) activity.findViewById(R.id.variationDeviationPopup);
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.variation_deviation_popup, viewGroup);

        final VariationDeviationPopup variationDeviationPopup = new VariationDeviationPopup(activity, Layout, HeadingError.variation);
        variationDeviationPopup.setContentView(Layout);
        variationDeviationPopup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        variationDeviationPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        variationDeviationPopup.setFocusable(true);
        variationDeviationPopup.SetValue(0);

        variationDeviationPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(variationDeviationPopup.result)
                {
                    Integer v = variationDeviationPopup.GetValue();
                    FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(activity);
                    flightPlanDataSource.open();
                    flightPlan.UpdateVariation((float)v);
                    flightPlanDataSource.UpdateInsertWaypoints(flightPlan.Waypoints);
                    flightPlanDataSource.close();

                    // Reload flightplan event...
                    if (onFlightplanEvent != null) onFlightplanEvent.onVariationClicked(waypoint, flightPlan);
                }
            }
        });

        variationDeviationPopup.showAtLocation(Layout, Gravity.CENTER, 0, 0);
    }

    public void DeleteWaypoint(final Waypoint waypoint)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Delete waypoint?");
        alertDialogBuilder
                .setMessage("Delete waypoint: " + waypoint.name + ", are you sure??")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(activity);
                        flightPlanDataSource.open();
                        flightPlanDataSource.deleteWaypoint(waypoint);
                        flightPlanDataSource.close();
                        if (waypoint.marker != null) waypoint.marker.remove();
                        flightPlan.Waypoints.remove(waypoint);
                        dialog.cancel();
                        if (onFlightplanEvent != null) onFlightplanEvent.onDeleteClickedClicked(waypoint, flightPlan);
                    }

                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void MoveWaypoint2(FlightPlan flightPlan)
    {
        //Update the database
        // TODO: Entries in the database
        // Change to order parameter
        if (onFlightplanEvent != null) onFlightplanEvent.onWaypointMoved(flightPlan);
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(activity);
        flightPlanDataSource.open();
        flightPlanDataSource.updateWaypointSortOrder(flightPlan);
        flightPlanDataSource.updateWaypointSortOrderDB(flightPlan);
        flightPlanDataSource.close();
    }


    public void SetDeviation(final Waypoint waypoint)
    {
        Log.i(TAG, "Deviation Button clicked: " + waypoint.name);
        int popupWidth = 200;
        int popupHeight = 300;

        LinearLayout viewGroup = (LinearLayout) activity.findViewById(R.id.variationDeviationPopup);
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.variation_deviation_popup, viewGroup);

        final VariationDeviationPopup deviationPopup = new VariationDeviationPopup(activity, Layout, HeadingError.deviation);
        deviationPopup.setContentView(Layout);
        deviationPopup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        deviationPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        deviationPopup.setFocusable(true);
        deviationPopup.SetValue(0);

        deviationPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(deviationPopup.result)
                {
                    Integer v = deviationPopup.GetValue();
                    FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(activity);
                    flightPlanDataSource.open();
                    waypoint.SetDeviation((float)v);
                    flightPlanDataSource.UpdateInsertWaypoints(flightPlan.Waypoints);
                    flightPlanDataSource.close();
                    //LoadFlightplanGrid();
                    if (onFlightplanEvent != null) onFlightplanEvent.onDeviationClicked(waypoint, flightPlan);
                }
            }
        });

        deviationPopup.showAtLocation(Layout, Gravity.CENTER, 0, 0);
    }

    public void SetETO(final Waypoint waypoint, Location curPlaneLocation)
    {
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(activity);
        flightPlanDataSource.open();
        flightPlanDataSource.calculateETO(new Date(), flightPlan);
        flightPlanDataSource.close();


        flightPlan.startFlightplan(curPlaneLocation);

        if (onFlightplanEvent != null) onFlightplanEvent.onTakeoffClicked(waypoint, flightPlan);
    }

    public void SetATO(final Waypoint waypoint, Location curPlaneLocation)
    {
        FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(activity);
        flightPlanDataSource.open();
        flightPlanDataSource.setATOcalculateRETO(waypoint, flightPlan);
        flightPlanDataSource.close();

        flightPlan.nextLeg(curPlaneLocation);

        if (onFlightplanEvent != null) onFlightplanEvent.onAtoClicked(waypoint, flightPlan);
    }
}

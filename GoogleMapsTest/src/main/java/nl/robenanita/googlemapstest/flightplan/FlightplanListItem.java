package nl.robenanita.googlemapstest.flightplan;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 11-12-2017.
 */

public class FlightplanListItem {
    public TextView flightplanCheckpointTxt;
    public TextView flightplanStationTxt;
    public TextView flightplanFrequencyTxt;
    public TextView flightplanRETOTxt;
    public TextView flightplanTimeLegTxt;
    public TextView flightplanTimeTotTxt;
    public TextView flightplanTrueHdgTxt;
    public TextView flightplanTrueTrackTxt;
    public TextView flightplanDistanceLegTxt;
    public TextView flightplanDistanceTotTxt;
    public TextView flightplanGroundSpeedTxt;
    public TextView flightplanWindSpeedDirTxt;
    public Button setVariationBtn;
    public Button setDeviationBtn;
    public Button setTakeOffTimeBtn;
    public Button setAtoBtn;
    public ImageButton dragHandleBtn;

    public FlightplanListItem(View view)
    {
        getControls(view);
    }

    private void getControls(View view)
    {
        flightplanCheckpointTxt = (TextView) view.findViewById(R.id.flightplanCheckpointTxt);
        flightplanStationTxt = (TextView) view.findViewById(R.id.flightplanStationTxt);
        flightplanFrequencyTxt = (TextView) view.findViewById(R.id.flightplanFrequencyTxt);
        flightplanRETOTxt = (TextView) view.findViewById(R.id.flightplanRETOTxt);
        flightplanTimeLegTxt = (TextView) view.findViewById(R.id.flightplanTimeLegTxt);
        flightplanTimeTotTxt = (TextView) view.findViewById(R.id.flightplanTimeTotTxt);
        flightplanTrueHdgTxt = (TextView) view.findViewById(R.id.flightplanTrueHdgTxt);
        flightplanTrueTrackTxt = (TextView) view.findViewById(R.id.flightplanTrueTrackTxt);
        flightplanDistanceLegTxt = (TextView) view.findViewById(R.id.flightplanDistanceLegTxt);
        flightplanDistanceTotTxt = (TextView) view.findViewById(R.id.flightplanDistanceTotTxt);
        flightplanGroundSpeedTxt = (TextView) view.findViewById(R.id.flightplanGroundSpeedTxt);
        flightplanWindSpeedDirTxt = (TextView) view.findViewById(R.id.flightplanWindSpeedDirTxt);

        setVariationBtn = (Button) view.findViewById(R.id.SetVariationBtn);
        setDeviationBtn = (Button) view.findViewById(R.id.setDeviationBtn);
        setDeviationBtn.setVisibility(View.INVISIBLE);

        setTakeOffTimeBtn = (Button) view.findViewById(R.id.setTakeoffTimeBtn);
        setAtoBtn = (Button) view.findViewById(R.id.setAtoBtn);

        dragHandleBtn = (ImageButton) view.findViewById(R.id.flightPlanItemDownImgBtn);
    }

    public Waypoint getWaypoint()
    {
        Waypoint waypoint = null;
        if (flightplanCheckpointTxt != null) {
            waypoint = (Waypoint) flightplanCheckpointTxt.getTag();
        }
        return waypoint;
    }

    public Waypoint setWaypointInfo(FlightPlan flightPlan, Integer position) {
        Waypoint waypoint = flightPlan.Waypoints.get(position);

        if ((flightPlan.getActivetoWaypointIndex() == position) && flightPlan.getFlightplanActive()) {
            flightplanCheckpointTxt.setTextColor(Color.RED);
            flightplanCheckpointTxt.setTypeface(null, Typeface.BOLD);
            setDeviationBtn.setTypeface(null, Typeface.BOLD);
            flightplanCheckpointTxt.setTextSize(20);
        }
        else
        {
            flightplanCheckpointTxt.setTypeface(null, Typeface.NORMAL);
            flightplanCheckpointTxt.setTextColor(0xFF383838);
            setDeviationBtn.setTypeface(null, Typeface.NORMAL);
            flightplanCheckpointTxt.setTextSize(14);
        }
        setAtoBtn.setEnabled((flightPlan.getActivetoWaypointIndex() == position) && flightPlan.getFlightplanActive());

        if (position==1) dragHandleBtn.setImageResource(R.drawable.drag_reorder_down);
        if (position==flightPlan.Waypoints.size()-2) dragHandleBtn.setImageResource(R.drawable.drag_reorder_up);
        if ((position!=1) && (position!=flightPlan.Waypoints.size()-2)) dragHandleBtn.setImageResource(R.drawable.drag_reorder);

        return setWaypointInfo(waypoint, flightPlan.getFlightplanActive());
    }

    public Waypoint setWaypointInfo(Waypoint waypoint, Boolean flightplanActive)
    {
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");

        flightplanCheckpointTxt.setText(waypoint.name);
        flightplanStationTxt.setText("");
        flightplanCheckpointTxt.setTag(waypoint);
        flightplanFrequencyTxt.setText(Double.toString(waypoint.frequency));
        flightplanRETOTxt.setText((waypoint.reto == null) ? "NA" : ft.format(waypoint.reto));
        flightplanTimeLegTxt.setText((waypoint.time_leg == 0) ? "" : waypoint.time_leg.toString() + " min");
        flightplanTimeTotTxt.setText((waypoint.time_total == 0) ? "" : waypoint.time_total.toString() + " min");
        flightplanTrueHdgTxt.setText((waypoint.true_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.true_heading)));
        flightplanTrueTrackTxt.setText((waypoint.true_track == 0d) ? "" : Integer.toString(Math.round(waypoint.true_track)));
        // Distance is in meters, so divide bij 1852 for Nautical Miles
        flightplanDistanceLegTxt.setText((waypoint.distance_leg == 0) ? "" : Integer.toString(Math.round((float) waypoint.distance_leg / 1852f)));
        flightplanDistanceTotTxt.setText((waypoint.distance_total == 0) ? "" : Integer.toString(Math.round((float) waypoint.distance_total / 1852f)));
        flightplanGroundSpeedTxt.setText((waypoint.ground_speed == 0) ? "" : waypoint.ground_speed.toString());
        flightplanWindSpeedDirTxt.setText((waypoint.wind_direction == 0) ? "" : Integer.toString(Math.round(waypoint.wind_direction))
                + "/" + ((waypoint.wind_speed == 0) ? "" : waypoint.wind_speed.toString()));

        setTakeOffTimeBtn.setEnabled((waypoint.waypointType == WaypointType.departudeAirport) && !flightplanActive);
        if ((waypoint.waypointType != WaypointType.departudeAirport))
            setTakeOffTimeBtn.setText((waypoint.eto == null) ? "NA" : ft.format(waypoint.eto));

        if ((waypoint.waypointType != WaypointType.departudeAirport))
            setAtoBtn.setText((waypoint.ato == null) ? "NA" : ft.format(waypoint.ato));

        setVariationBtn.setEnabled((waypoint.waypointType == WaypointType.departudeAirport));

        if ((waypoint.waypointType != WaypointType.departudeAirport))
            setVariationBtn.setText((waypoint.magnetic_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.magnetic_heading)));

        if (waypoint.waypointType != WaypointType.departudeAirport) {
            setDeviationBtn.setVisibility(View.VISIBLE);
            setDeviationBtn.setText((waypoint.compass_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.compass_heading)));

        }
        return waypoint;
    }
}

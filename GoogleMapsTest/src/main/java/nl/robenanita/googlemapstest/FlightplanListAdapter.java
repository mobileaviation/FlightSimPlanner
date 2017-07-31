package nl.robenanita.googlemapstest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.Waypoint;
import nl.robenanita.googlemapstest.flightplan.WaypointType;

/**
 * Created by Rob Verhoef on 9-4-14.
 */
public class FlightplanListAdapter extends BaseAdapter {
    //private ArrayList<Waypoint> waypoints;
    private FlightPlan flightPlan;
    private String TAG = "GooglemapsTest";
    private Waypoint waypoint;
    public NavigationActivity navigationActivity;

    //public FlighplanListAdapter()
    //{
    //    super();
   // }
    public FlightplanListAdapter(FlightPlan flightPlan) {
        this.flightPlan = flightPlan;
    }

    @Override
    public int getCount() {
        int s = flightPlan.Waypoints.size();
        return flightPlan.showOnlyActive ? 1 : s;
    }

    @Override
    public Object getItem(int i) {
        int s = i;
        if (flightPlan.showOnlyActive) s = flightPlan.getActivetoWaypointIndex();
        return flightPlan.Waypoints.get(s);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.flightplan_item, viewGroup, false);

        int index = flightPlan.showOnlyActive ? flightPlan.getActivetoWaypointIndex() : i;

        if (view.getVisibility() == View.VISIBLE) {
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm");

            TextView flightplanCheckpointTxt = (TextView) view.findViewById(R.id.flightplanCheckpointTxt);
            TextView flightplanStationTxt = (TextView) view.findViewById(R.id.flightplanStationTxt);
            TextView flightplanFrequencyTxt = (TextView) view.findViewById(R.id.flightplanFrequencyTxt);

            //TextView flightplanATOTxt = (TextView) view.findViewById(R.id.flightplanATOTxt);
            TextView flightplanRETOTxt = (TextView) view.findViewById(R.id.flightplanRETOTxt);
            TextView flightplanTimeLegTxt = (TextView) view.findViewById(R.id.flightplanTimeLegTxt);
            TextView flightplanTimeTotTxt = (TextView) view.findViewById(R.id.flightplanTimeTotTxt);
            //TextView flightplanCompassHdgTxt = (TextView) view.findViewById(R.id.flightplanCompassHdgTxt);
            //TextView flightplanMagneticHdgTxt = (TextView) view.findViewById(R.id.flightplanMagneticHdgTxt);
            TextView flightplanTrueHdgTxt = (TextView) view.findViewById(R.id.flightplanTrueHdgTxt);
            TextView flightplanTrueTrackTxt = (TextView) view.findViewById(R.id.flightplanTrueTrackTxt);
            TextView flightplanDistanceLegTxt = (TextView) view.findViewById(R.id.flightplanDistanceLegTxt);
            TextView flightplanDistanceTotTxt = (TextView) view.findViewById(R.id.flightplanDistanceTotTxt);
            TextView flightplanGroundSpeedTxt = (TextView) view.findViewById(R.id.flightplanGroundSpeedTxt);
            TextView flightplanWindSpeedDirTxt = (TextView) view.findViewById(R.id.flightplanWindSpeedDirTxt);

            Button setVariationBtn = (Button) view.findViewById(R.id.SetVariationBtn);
            //setVariationBtn.setVisibility(View.GONE);
            Button setDeviationBtn = (Button) view.findViewById(R.id.setDeviationBtn);
            setDeviationBtn.setVisibility(View.INVISIBLE);

            Button setTakeOffTimeBtn = (Button) view.findViewById(R.id.setTakeoffTimeBtn);
            Button setAtoBtn = (Button) view.findViewById(R.id.setAtoBtn);
            ImageButton moveDownBtn = (ImageButton) view.findViewById(R.id.flightPlanItemDownImgBtn);
            ImageButton moveUpBtn = (ImageButton) view.findViewById(R.id.flightPlanItemUpImgBtn);

            waypoint = flightPlan.Waypoints.get(index);
            flightplanCheckpointTxt.setText(waypoint.name);
            if ((flightPlan.getActivetoWaypointIndex() == index) && flightPlan.getFlightplanActive()) {
                flightplanCheckpointTxt.setTextColor(Color.RED);
                flightplanCheckpointTxt.setTypeface(flightplanCheckpointTxt.getTypeface(), Typeface.BOLD);
                flightplanCheckpointTxt.setTextSize(20);
            }

            flightplanStationTxt.setText("");
            flightplanFrequencyTxt.setText(Double.toString(waypoint.frequency));
            //flightplanETOTxt.setText((waypoint.eto == 0) ? "" : waypoint.eto.toString());
            //flightplanATOTxt.setText((waypoint.ato == 0) ? "" : waypoint.ato.toString());
            flightplanRETOTxt.setText((waypoint.reto == null) ? "NA" : ft.format(waypoint.reto));
            flightplanTimeLegTxt.setText((waypoint.time_leg == 0) ? "" : waypoint.time_leg.toString() + " min");
            flightplanTimeTotTxt.setText((waypoint.time_total == 0) ? "" : waypoint.time_total.toString() + " min");
            //flightplanCompassHdgTxt.setText((waypoint.compass_heading == 0d) ? "" : Double.toString(Math.round(waypoint.compass_heading)));
            //flightplanMagneticHdgTxt.setText((waypoint.magnetic_heading == 0d) ? "" : Double.toString(Math.round(waypoint.magnetic_heading)));
            flightplanTrueHdgTxt.setText((waypoint.true_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.true_heading)));
            flightplanTrueTrackTxt.setText((waypoint.true_track == 0d) ? "" : Integer.toString(Math.round(waypoint.true_track)));
            // Distance is in meters, so divide bij 1852 for Nautical Miles
            flightplanDistanceLegTxt.setText((waypoint.distance_leg == 0) ? "" : Integer.toString(Math.round((float) waypoint.distance_leg / 1852f)));
            flightplanDistanceTotTxt.setText((waypoint.distance_total == 0) ? "" : Integer.toString(Math.round((float) waypoint.distance_total / 1852f)));
            flightplanGroundSpeedTxt.setText((waypoint.ground_speed == 0) ? "" : waypoint.ground_speed.toString());
            flightplanWindSpeedDirTxt.setText((waypoint.wind_direction == 0) ? "" : Integer.toString(Math.round(waypoint.wind_direction))
                    + "/" + ((waypoint.wind_speed == 0) ? "" : waypoint.wind_speed.toString()));

            setTakeOffTimeBtn.setEnabled((waypoint.waypointType == WaypointType.departudeAirport) && !flightPlan.getFlightplanActive());
            if ((waypoint.waypointType != WaypointType.departudeAirport))
                setTakeOffTimeBtn.setText((waypoint.eto == null) ? "NA" : ft.format(waypoint.eto));

            setAtoBtn.setEnabled((waypoint.waypointType != WaypointType.departudeAirport) && flightPlan.getFlightplanActive());
            if ((waypoint.waypointType != WaypointType.departudeAirport))
                setAtoBtn.setText((waypoint.ato == null) ? "NA" : ft.format(waypoint.ato));

            setAtoBtn.setEnabled((flightPlan.getActivetoWaypointIndex() == index) && flightPlan.getFlightplanActive());

            setVariationBtn.setEnabled((waypoint.waypointType == WaypointType.departudeAirport));

            Log.i(TAG, "Magnetic heading: " + waypoint.magnetic_heading );
            if ((waypoint.waypointType != WaypointType.departudeAirport))
                setVariationBtn.setText((waypoint.magnetic_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.magnetic_heading)));

            if (waypoint.waypointType != WaypointType.departudeAirport) {
                setDeviationBtn.setVisibility(View.VISIBLE);
                //flightplanCompassHdgTxt.setVisibility(View.GONE);
                setDeviationBtn.setText((waypoint.compass_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.compass_heading)));
            }

            setVariationBtn.setTag(waypoint);
            setVariationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Waypoint p = (Waypoint) ((Button) view).getTag();
                    if (onWaypointEvent != null) onWaypointEvent.onVariationClicked(p);
                    //navigationActivity.VariationClick(p);
                }
            });

            setDeviationBtn.setTag(waypoint);
            setDeviationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Waypoint p = (Waypoint) ((Button) view).getTag();
                    if (onWaypointEvent != null) onWaypointEvent.onDeviationClicked(p);
                    //navigationActivity.DeviationClick(p);
                }
            });

            setTakeOffTimeBtn.setTag(waypoint);
            setTakeOffTimeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Waypoint p = (Waypoint) ((Button) view).getTag();
                    if (onWaypointEvent != null) onWaypointEvent.onTakeoffClicked(p);
                    //navigationActivity.ETOClick(p);
                }
            });

            setAtoBtn.setTag(waypoint);
            setAtoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Waypoint p = (Waypoint) ((Button) view).getTag();
                    if (onWaypointEvent != null) onWaypointEvent.onAtoClicked(p);
                    //navigationActivity.ATOClick(p);
                }
            });


            moveDownBtn.setTag(waypoint);
            moveDownBtn.setVisibility(((waypoint.waypointType == WaypointType.departudeAirport)
                    || (waypoint.waypointType == WaypointType.destinationAirport)) || flightPlan.getFlightplanActive()
                    ? View.INVISIBLE : View.VISIBLE);
            if (index == flightPlan.Waypoints.size() - 2) moveDownBtn.setVisibility(View.INVISIBLE);

            moveUpBtn.setTag(waypoint);
            moveUpBtn.setVisibility(((waypoint.waypointType == WaypointType.departudeAirport)
                    || (waypoint.waypointType == WaypointType.destinationAirport)) || flightPlan.getFlightplanActive()
                    ? View.INVISIBLE : View.VISIBLE);
            if (index == 1) moveUpBtn.setVisibility(View.INVISIBLE);

            moveDownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Waypoint p = (Waypoint) ((ImageButton) view).getTag();
                    if (onWaypointEvent != null) onWaypointEvent.onMoveDownClicked(p);
                    //navigationActivity.moveWaypoint(flightPlan, p, true);
                }
            });
            moveDownBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Waypoint p = (Waypoint) ((ImageButton) view).getTag();
                    if (onWaypointEvent != null) onWaypointEvent.onDeleteClickedClicked(p);
                    //navigationActivity.deleteWaypoint(p);
                    return false;
                }
            });

            moveUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Waypoint p = (Waypoint) ((ImageButton) view).getTag();
                    if (onWaypointEvent != null) onWaypointEvent.onMoveUpClicked(p);
                    //navigationActivity.moveWaypoint(flightPlan, p, false);
                }
            });
            moveUpBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Waypoint p = (Waypoint) ((ImageButton) view).getTag();
                    //navigationActivity.deleteWaypoint(p);
                    if (onWaypointEvent != null) onWaypointEvent.onDeleteClickedClicked(p);
                    return false;
                }
            });
        }
        return view;
    }

    private OnWaypointEvent onWaypointEvent = null;
    public void setOnFlightplanEvent( final OnWaypointEvent d) {onWaypointEvent = d; }
    public interface OnWaypointEvent {
        public void onVariationClicked(Waypoint waypoint);
        public void onDeviationClicked(Waypoint waypoint);
        public void onTakeoffClicked(Waypoint waypoint);
        public void onAtoClicked(Waypoint waypoint);
        public void onMoveUpClicked(Waypoint waypoint);
        public void onMoveDownClicked(Waypoint waypoint);
        public void onDeleteClickedClicked(Waypoint waypoint);
    }

}

package nl.robenanita.googlemapstest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.Waypoint;
import nl.robenanita.googlemapstest.flightplan.WaypointType;

/**
 * Created by Rob Verhoef on 9-4-14.
 */
public class FlightplanListAdapter extends DragItemAdapter<Waypoint, FlightplanListAdapter.ViewHolder> {
    //private ArrayList<Waypoint> waypoints;
    private FlightPlan flightPlan;
    private String TAG = "GooglemapsTest";
    private Waypoint waypoint;

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    public FlightplanListAdapter(FlightPlan flightPlan, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.flightPlan = flightPlan;

        setItemList(flightPlan.Waypoints);
    }

    @Override
    public long getUniqueItemId(int position) {
        return flightPlan.Waypoints.get(position).order;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        waypoint = flightPlan.Waypoints.get(position);
        holder.flightplanCheckpointTxt.setText(waypoint.name);
        holder.flightplanCheckpointTxt.setClickable(true);
        holder.flightplanCheckpointTxt.setTag(waypoint);
        holder.flightplanCheckpointTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((TextView) view).getTag();
                Log.i(TAG, "Waypoint: " + waypoint.name + " Clicked!");
                if (onWaypointEvent != null) onWaypointEvent.onWaypointClicked(p);
            }
        });

        if ((flightPlan.getActivetoWaypointIndex() == position) && flightPlan.getFlightplanActive()) {
            holder.flightplanCheckpointTxt.setTextColor(Color.RED);
            holder.flightplanCheckpointTxt.setTypeface(holder.flightplanCheckpointTxt.getTypeface(), Typeface.BOLD);
            holder.flightplanCheckpointTxt.setTextSize(20);
        }

        holder.flightplanStationTxt.setText("");
        holder.flightplanFrequencyTxt.setText(Double.toString(waypoint.frequency));
        holder.flightplanRETOTxt.setText((waypoint.reto == null) ? "NA" : ft.format(waypoint.reto));
        holder.flightplanTimeLegTxt.setText((waypoint.time_leg == 0) ? "" : waypoint.time_leg.toString() + " min");
        holder.flightplanTimeTotTxt.setText((waypoint.time_total == 0) ? "" : waypoint.time_total.toString() + " min");
        holder.flightplanTrueHdgTxt.setText((waypoint.true_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.true_heading)));
        holder.flightplanTrueTrackTxt.setText((waypoint.true_track == 0d) ? "" : Integer.toString(Math.round(waypoint.true_track)));
        // Distance is in meters, so divide bij 1852 for Nautical Miles
        holder.flightplanDistanceLegTxt.setText((waypoint.distance_leg == 0) ? "" : Integer.toString(Math.round((float) waypoint.distance_leg / 1852f)));
        holder.flightplanDistanceTotTxt.setText((waypoint.distance_total == 0) ? "" : Integer.toString(Math.round((float) waypoint.distance_total / 1852f)));
        holder.flightplanGroundSpeedTxt.setText((waypoint.ground_speed == 0) ? "" : waypoint.ground_speed.toString());
        holder.flightplanWindSpeedDirTxt.setText((waypoint.wind_direction == 0) ? "" : Integer.toString(Math.round(waypoint.wind_direction))
                + "/" + ((waypoint.wind_speed == 0) ? "" : waypoint.wind_speed.toString()));

        holder.setTakeOffTimeBtn.setEnabled((waypoint.waypointType == WaypointType.departudeAirport) && !flightPlan.getFlightplanActive());
        if ((waypoint.waypointType != WaypointType.departudeAirport))
            holder.setTakeOffTimeBtn.setText((waypoint.eto == null) ? "NA" : ft.format(waypoint.eto));

        holder.setAtoBtn.setEnabled((waypoint.waypointType != WaypointType.departudeAirport) && flightPlan.getFlightplanActive());
        if ((waypoint.waypointType != WaypointType.departudeAirport))
            holder.setAtoBtn.setText((waypoint.ato == null) ? "NA" : ft.format(waypoint.ato));

        holder.setAtoBtn.setEnabled((flightPlan.getActivetoWaypointIndex() == position) && flightPlan.getFlightplanActive());

        holder.setVariationBtn.setEnabled((waypoint.waypointType == WaypointType.departudeAirport));

        Log.i(TAG, "Magnetic heading: " + waypoint.magnetic_heading );
        if ((waypoint.waypointType != WaypointType.departudeAirport))
            holder.setVariationBtn.setText((waypoint.magnetic_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.magnetic_heading)));

        if (waypoint.waypointType != WaypointType.departudeAirport) {
            holder.setDeviationBtn.setVisibility(View.VISIBLE);
            holder.setDeviationBtn.setText((waypoint.compass_heading == 0d) ? "" : Integer.toString(Math.round(waypoint.compass_heading)));

        }

        setButtonListeners(holder, position);
    }

    private void setButtonListeners(ViewHolder holder, int position)
    {
        holder.setVariationBtn.setTag(waypoint);
        holder.setVariationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onVariationClicked(p);
                //navigationActivity.VariationClick(p);
            }
        });

        holder.setDeviationBtn.setTag(waypoint);
        holder.setDeviationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onDeviationClicked(p);
            }
        });

        holder.setTakeOffTimeBtn.setTag(waypoint);
        holder.setTakeOffTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onTakeoffClicked(p);
            }
        });

        holder.setAtoBtn.setTag(waypoint);
        holder.setAtoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onAtoClicked(p);
            }
        });

        holder.dragHandleBtn.setTag(waypoint);
        holder.dragHandleBtn.setVisibility(((waypoint.waypointType == WaypointType.departudeAirport)
                || (waypoint.waypointType == WaypointType.destinationAirport))
                ? View.INVISIBLE : View.VISIBLE);

    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView flightplanCheckpointTxt;
        TextView flightplanStationTxt;
        TextView flightplanFrequencyTxt;
        TextView flightplanRETOTxt;
        TextView flightplanTimeLegTxt;
        TextView flightplanTimeTotTxt;
        TextView flightplanTrueHdgTxt;
        TextView flightplanTrueTrackTxt;
        TextView flightplanDistanceLegTxt;
        TextView flightplanDistanceTotTxt;
        TextView flightplanGroundSpeedTxt;
        TextView flightplanWindSpeedDirTxt;
        Button setVariationBtn;
        Button setDeviationBtn;
        Button setTakeOffTimeBtn;
        Button setAtoBtn;
        ImageButton dragHandleBtn;


        ViewHolder(final View view) {
            super(view, mGrabHandleId, mDragOnLongPress);

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

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private OnWaypointEvent onWaypointEvent = null;
    public void setOnFlightplanEvent( final OnWaypointEvent d) {onWaypointEvent = d; }
    public interface OnWaypointEvent {
        public void onVariationClicked(Waypoint waypoint);
        public void onDeviationClicked(Waypoint waypoint);
        public void onTakeoffClicked(Waypoint waypoint);
        public void onAtoClicked(Waypoint waypoint);
        public void onDeleteClickedClicked(Waypoint waypoint);
        public void onWaypointClicked(Waypoint waypoint);
    }

}

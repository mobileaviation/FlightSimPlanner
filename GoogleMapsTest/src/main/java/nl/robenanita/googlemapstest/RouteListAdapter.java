package nl.robenanita.googlemapstest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;

import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Route.RouteListItem;
import nl.robenanita.googlemapstest.Route.Waypoint;
import nl.robenanita.googlemapstest.Route.WaypointType;

/**
 * Created by Rob Verhoef on 9-4-14.
 */
public class RouteListAdapter extends DragItemAdapter<Waypoint, RouteListAdapter.ViewHolder> {
    //private ArrayList<Waypoint> waypoints;
    private Route flightPlan;
    private String TAG = "GooglemapsTest";

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    public RouteListAdapter(Route flightPlan, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.flightPlan = flightPlan;

        setItemList(flightPlan.Waypoints);
    }

    private Integer item_height = 0;
    public Integer getHeight(Context context)
    {
        Integer vi = (getItemCount() * 55) + 35;
        Float v = Float.valueOf(vi.toString());
        Integer r = Math.round(Helpers.convertDpToPixel (v, context));
        //return Math.round(Helpers.convertPixelsToDp (v, context));
        return r;
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
        Waypoint waypoint = holder.item.setWaypointInfo(flightPlan, position);

        holder.item.flightplanCheckpointTxt.setClickable(true);
        holder.item.flightplanCheckpointTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((TextView) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onWaypointClicked(p);
            }
        });

        setButtonListeners(holder, waypoint);

        item_height = holder.item.getHeight();
    }

    private void setButtonListeners(ViewHolder holder, Waypoint waypoint)
    {
        holder.item.setVariationBtn.setTag(waypoint);
        holder.item.setVariationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onVariationClicked(p);
            }
        });

        holder.item.setDeviationBtn.setTag(waypoint);
        holder.item.setDeviationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onDeviationClicked(p);
            }
        });

        holder.item.setTakeOffTimeBtn.setTag(waypoint);
        holder.item.setTakeOffTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onTakeoffClicked(p);
            }
        });

        holder.item.setAtoBtn.setTag(waypoint);
        holder.item.setAtoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Waypoint p = (Waypoint) ((Button) view).getTag();
                if (onWaypointEvent != null) onWaypointEvent.onAtoClicked(p);
            }
        });

        holder.item.dragHandleBtn.setTag(waypoint);
        holder.item.dragHandleBtn.setVisibility(((waypoint.waypointType == WaypointType.departudeAirport)
                || (waypoint.waypointType == WaypointType.destinationAirport))
                ? View.INVISIBLE : View.VISIBLE);

    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        RouteListItem item;


        ViewHolder(final View view) {
            super(view, mGrabHandleId, mDragOnLongPress);
            item = new RouteListItem(view);
        }

        @Override
        public void onItemClicked(View view) {

        }

        @Override
        public boolean onItemLongClicked(View view) {

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

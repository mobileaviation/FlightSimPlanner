package nl.robenanita.googlemapstest.MapFragment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 28-8-2017.
 */

public class AddWaypointAdapter extends BaseAdapter {
    private static int selectedIndex = -1;
    public ArrayList<SelectableWaypoint> waypoints;

    public AddWaypointAdapter(ArrayList<SelectableWaypoint> waypoints)
    {
        this.waypoints = waypoints;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.add_waypoint_item, viewGroup, false);

        TextView airportIdentTxt = (TextView) view.findViewById(R.id.addWaypointAirportIdentTxt);
        TextView airportNameTxt = (TextView) view.findViewById(R.id.addWaypointAirportNameTxt);
        LinearLayout addWaypointLayout = (LinearLayout) view.findViewById(R.id.addWaypointAdapterLayout);
        ImageView iconView = (ImageView) view.findViewById(R.id.addWaypointIconImage);

        SelectableWaypoint waypoint = waypoints.get(i);
        airportIdentTxt.setText(waypoint.GetIdent());
        airportNameTxt.setText(waypoint.GetName());

        iconView.setImageBitmap(waypoint.GetIcon(viewGroup.getContext()));

        if ( (i & 1) == 0 ) {
            addWaypointLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public SelectableWaypoint GetSelectedWaypoint(int i)
    {
        return (SelectableWaypoint) waypoints.get(i);
    }

    @Override
    public int getCount() {
        return waypoints.size();
    }
}

package nl.robenanita.googlemapstest;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.OnFlightplanEvent;
import nl.robenanita.googlemapstest.flightplan.Waypoint;

public class FlightplanGrid extends Fragment {



    public FlightplanGrid() {
        // Required empty public constructor
    }

    private String TAG = "GooglemapsTest";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    ListView flightplanItemsList;
    ImageButton closeButton;
    CheckBox onlyActiveCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_flightplan_grid, container, false);

        flightplanItemsList = (ListView) v.findViewById(R.id.flightplanItemsList);
        closeButton = (ImageButton) v.findViewById(R.id.closeFlightplanButton);
        onlyActiveCheckBox = (CheckBox) v.findViewById(R.id.onlyActiveCheckBox);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private FlightPlan flightPlan;
    public void LoadFlightplanGrid(FlightPlan flightPlan)
    {
        this.flightPlan = flightPlan;

        onlyActiveCheckBox.setEnabled(flightPlan.getFlightplanActive());
        onlyActiveCheckBox.setChecked(flightPlan.showOnlyActive);

        setUpAdapter();
        //setOnlyActiveinView(flightPlan.showOnlyActive);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFlightplanEvent != null) onFlightplanEvent.onClosePlanClicked(FlightplanGrid.this.flightPlan);
            }
        });

        onlyActiveCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean c = onlyActiveCheckBox.isChecked();
                FlightplanGrid.this.flightPlan.showOnlyActive = c;
                setUpAdapter();
                //setOnlyActiveinView(c);
            }
        });
    }

    public void ReloadFlightplan()
    {
        flightplanItemsList.invalidateViews();
    }

    private FlightplanListAdapter adapter;
    private void setUpAdapter()
    {
        int p = flightplanItemsList.getVerticalScrollbarPosition();
        Log.i(TAG, "Flightplan vertical grid position = " + Integer.toString(p));

        adapter = new FlightplanListAdapter(flightPlan);
        //adapter.navigationActivity = this;

        adapter.setOnFlightplanEvent(new FlightplanListAdapter.OnWaypointEvent() {
            @Override
            public void onVariationClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onVariationClicked(waypoint, flightPlan);
            }

            @Override
            public void onDeviationClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onDeviationClicked(waypoint, flightPlan);
            }

            @Override
            public void onTakeoffClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onTakeoffClicked(waypoint, flightPlan);
            }

            @Override
            public void onAtoClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onAtoClicked(waypoint, flightPlan);
            }

            @Override
            public void onMoveUpClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onMoveUpClicked(waypoint, flightPlan);
            }

            @Override
            public void onMoveDownClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onMoveDownClicked(waypoint, flightPlan);
            }

            @Override
            public void onDeleteClickedClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onDeleteClickedClicked(waypoint, flightPlan);
            }
        });

        flightplanItemsList.setAdapter(adapter);
        flightplanItemsList.setVerticalScrollbarPosition(p);
        adapter.notifyDataSetChanged();

        //setGridHeight(adapter);
    }

    private void setGridHeight(FlightplanListAdapter adapter)
    {
        if(adapter.getCount() > 3){
            View item = adapter.getView(0, null, flightplanItemsList);
            item.measure(0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            flightplanItemsList.setLayoutParams(params);
        }
    }

    private void setOnlyActiveinView(boolean Onlyactive)
    {
        FlightplanListAdapter adapter = (FlightplanListAdapter)flightplanItemsList.getAdapter();
        if (Onlyactive) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            flightplanItemsList.setLayoutParams(params);
        }
        else
        {
            setGridHeight(adapter);
        }

    }

    private OnFlightplanEvent onFlightplanEvent = null;
    public void setOnFlightplanEvent( final OnFlightplanEvent d) {onFlightplanEvent = d; }
}

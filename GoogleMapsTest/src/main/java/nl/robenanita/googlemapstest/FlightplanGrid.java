package nl.robenanita.googlemapstest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;
import com.woxthebox.draglistview.swipe.ListSwipeHelper;
import com.woxthebox.draglistview.swipe.ListSwipeItem;

import nl.robenanita.googlemapstest.flightplan.FlightPlan;
import nl.robenanita.googlemapstest.flightplan.FlightplanListItem;
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

    DragListView flightplanItemsList;
    ImageButton closeButton;
    CheckBox onlyActiveCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_flightplan_grid, container, false);

        flightplanItemsList = (DragListView) v.findViewById(R.id.flightplanItemsList);
        flightplanItemsList.getRecyclerView().setVerticalScrollBarEnabled(true);
        flightplanItemsList.setCanNotDragAboveTopItem(true);
        flightplanItemsList.setCanNotDragBelowBottomItem(true);
        flightplanItemsList.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    if (onFlightplanEvent != null) onFlightplanEvent.onWaypointMoved(FlightplanGrid.this.flightPlan);
                }
            }
        });

//        flightplanItemsList.setSwipeListener(new ListSwipeHelper.OnSwipeListener() {
//            @Override
//            public void onItemSwipeStarted(ListSwipeItem item) {
//
//            }
//
//            @Override
//            public void onItemSwipeEnded(ListSwipeItem item, ListSwipeItem.SwipeDirection swipedDirection) {
//
//            }
//
//            @Override
//            public void onItemSwiping(ListSwipeItem item, float swipedDistanceX) {
//
//            }
//        });


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
            }
        });
    }

    public void ReloadFlightplan()
    {
        //flightplanItemsList.notify();
        //flightplanItemsList.invalidate();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            FlightplanListItem clickedItem = new FlightplanListItem(clickedView);
            Waypoint waypoint = clickedItem.getWaypoint();
            FlightplanListItem dragItem = new FlightplanListItem(dragView);
            dragItem.setWaypointInfo(waypoint, false);
        }
    }

    private FlightplanListAdapter adapter;
    private void setUpAdapter()
    {
        int p = flightplanItemsList.getVerticalScrollbarPosition();
        Log.i(TAG, "Flightplan vertical grid position = " + Integer.toString(p));

        adapter = new FlightplanListAdapter(flightPlan, R.layout.flightplan_item, R.id.flightPlanItemDownImgBtn, false);
        flightplanItemsList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

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
            public void onDeleteClickedClicked(Waypoint waypoint) {
                if (onFlightplanEvent != null) onFlightplanEvent.onDeleteClickedClicked(waypoint, flightPlan);
            }

            @Override
            public void onWaypointClicked(Waypoint waypoint){
                if (onFlightplanEvent != null) onFlightplanEvent.onWaypointClicked(waypoint);
            }
        });

        flightplanItemsList.setAdapter(adapter, true);
        flightplanItemsList.setVerticalScrollbarPosition(p);
        flightplanItemsList.setCanDragHorizontally(false);
        flightplanItemsList.setCustomDragItem(new MyDragItem(this.getActivity(), R.layout.flightplan_item));
        adapter.notifyDataSetChanged();
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
            //setGridHeight(adapter);
        }

    }

    private OnFlightplanEvent onFlightplanEvent = null;
    public void setOnFlightplanEvent( final OnFlightplanEvent d) {onFlightplanEvent = d; }
}

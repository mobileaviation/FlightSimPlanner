package nl.robenanita.googlemapstest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Route.RouteListItem;
import nl.robenanita.googlemapstest.Route.OnRouteEvent;
import nl.robenanita.googlemapstest.Route.Waypoint;

public class RouteGrid extends Fragment {
    public RouteGrid() {
        // Required empty public constructor
    }

    private String TAG = "GooglemapsTest";
    private View gridView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = view;
    }

    public Integer getHeight()
    {
        if (adapter != null) return adapter.getHeight();
        else
            return 0;
    }

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
        View v = inflater.inflate(R.layout.fragment_route_grid, container, false);

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
                    if (onFlightplanEvent != null) onFlightplanEvent.onWaypointMoved(RouteGrid.this.flightPlan);
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


    private Route flightPlan;
    public void LoadFlightplanGrid(Route flightPlan)
    {
        this.flightPlan = flightPlan;

        onlyActiveCheckBox.setEnabled(flightPlan.getFlightplanActive());
        onlyActiveCheckBox.setChecked(flightPlan.showOnlyActive);

        setUpAdapter();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFlightplanEvent != null) onFlightplanEvent.onClosePlanClicked(RouteGrid.this.flightPlan);
            }
        });

        onlyActiveCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean c = onlyActiveCheckBox.isChecked();
                RouteGrid.this.flightPlan.showOnlyActive = c;
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
            RouteListItem clickedItem = new RouteListItem(clickedView);
            Waypoint waypoint = clickedItem.getWaypoint();
            RouteListItem dragItem = new RouteListItem(dragView);
            dragItem.setWaypointInfo(waypoint, false);
        }
    }

    private RouteListAdapter adapter;
    private void setUpAdapter()
    {
        int p = flightplanItemsList.getVerticalScrollbarPosition();
        Log.i(TAG, "Flightplan vertical grid position = " + Integer.toString(p));

        adapter = new RouteListAdapter(flightPlan, R.layout.route_item, R.id.flightPlanItemDownImgBtn, false);
        flightplanItemsList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter.setOnFlightplanEvent(new RouteListAdapter.OnWaypointEvent() {
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
        flightplanItemsList.setCustomDragItem(new MyDragItem(this.getActivity(), R.layout.route_item));
        adapter.notifyDataSetChanged();
    }

    private void setOnlyActiveinView(boolean Onlyactive)
    {
        RouteListAdapter adapter = (RouteListAdapter)flightplanItemsList.getAdapter();
        if (Onlyactive) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            flightplanItemsList.setLayoutParams(params);
        }
        else
        {
            //setGridHeight(adapter);
        }

    }

    private OnRouteEvent onFlightplanEvent = null;
    public void setOnFlightplanEvent( final OnRouteEvent d) {onFlightplanEvent = d; }
}

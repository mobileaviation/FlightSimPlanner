package nl.robenanita.googlemapstest.InfoWindows;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import nl.robenanita.googlemapstest.MapFragment.SelectableWaypoint;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.flightplan.Waypoint;

public class WaypointFragment extends Fragment {
    public WaypointFragment() {
        // Required empty public constructor
    }

    private View view;
    private Waypoint waypoint;
    private DeleteWaypointListener deleteWaypointListener;
    public void SetOnDeleteWaypointListener(DeleteWaypointListener deleteWaypointListener)
    {
        this.deleteWaypointListener = deleteWaypointListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_waypoint, container, false);
        return view;
    }

    private Button deleteWaypointBtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deleteWaypointBtn = (Button) view.findViewById(R.id.waypointDelBtn);
        setDeleteBtnListener();
    }

    private void setDeleteBtnListener()
    {
        deleteWaypointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteWaypointListener != null) deleteWaypointListener.OnDeleteWaypoint(waypoint);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setupWaypoint(Waypoint waypoint)
    {
        this.waypoint = waypoint;
    }
}

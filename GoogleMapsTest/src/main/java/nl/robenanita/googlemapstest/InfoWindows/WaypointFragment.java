package nl.robenanita.googlemapstest.InfoWindows;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.flightplan.Waypoint;

public class WaypointFragment extends Fragment {
    public WaypointFragment() {
        // Required empty public constructor
    }

    private View view;
    private Waypoint waypoint;
    private UpdateWaypointListener deleteWaypointListener;
    public void SetOnDeleteWaypointListener(UpdateWaypointListener deleteWaypointListener)
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

    private ImageButton deleteWaypointBtn;
    private ImageButton moveupBtn;
    private ImageButton modedownBtn;
    private TextView wapoinyTitleTxt;
    private TextView headingTxt;
    private TextView distanceTxt;
    private TextView timeTxt;
    private EditText nameEdit;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deleteWaypointBtn = (ImageButton) view.findViewById(R.id.waypointDelBtn);
        moveupBtn = (ImageButton) view.findViewById(R.id.waypointUpBtn);
        modedownBtn = (ImageButton) view.findViewById(R.id.waypointDownBtn);
        wapoinyTitleTxt = (TextView) view.findViewById(R.id.wapointTitleTxt);
        headingTxt = (TextView) view.findViewById(R.id.waypointHeadingTxt);
        distanceTxt = (TextView) view.findViewById(R.id.waypointDistanceTxt);
        timeTxt = (TextView) view.findViewById(R.id.waypointTimeTxt);
        nameEdit = (EditText) view.findViewById(R.id.waypointNameEdit);

        wapoinyTitleTxt.setText("Waypoint Information");
        headingTxt.setText(Float.toString(waypoint.compass_heading) + "\u00b0");
        distanceTxt.setText(waypoint.distance_leg.toString() + " NM");
        timeTxt.setText(waypoint.time_leg.toString() + " min");

        nameEdit.setText(waypoint.name);

        setBtnListeners();
    }

    private void setBtnListeners()
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

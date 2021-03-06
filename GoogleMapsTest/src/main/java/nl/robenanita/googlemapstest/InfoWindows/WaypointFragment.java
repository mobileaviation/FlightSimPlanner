package nl.robenanita.googlemapstest.InfoWindows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Route.Waypoint;

public class WaypointFragment extends Fragment {
    public WaypointFragment() {
        // Required empty public constructor
    }

    private View view;
    private Waypoint waypoint;
    private UpdateWaypointListener waypointListener;
    private Activity mainActivity;

    public void SetActivity(Activity activity)
    {
        this.mainActivity = activity;
    }


    public void SetOnWaypointListener(UpdateWaypointListener waypointListener)
    {
        this.waypointListener = waypointListener;
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
    private TextView wapoinyTitleTxt;
    private TextView headingTxt;
    private TextView distanceTxt;
    private TextView timeTxt;
    private EditText nameEdit;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deleteWaypointBtn = (ImageButton) view.findViewById(R.id.waypointDelBtn);
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
                showAlert(20, waypoint);
            }
        });
        nameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /* handle action here */
                    if (waypointListener != null) waypointListener.OnRenameWaypoint(waypoint, textView.getText().toString());
                    handled = true;
                }
                return handled;
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

    private void showAlert(final int position, final Waypoint waypoint) {
        new AlertDialog.Builder(mainActivity)
                .setTitle("Delete waypoint")
                .setMessage("Are you sure you want to delete this waypoint?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //  deleteSuggestions(position);
                        if (waypointListener != null) waypointListener.OnDeleteWaypoint(waypoint);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (waypointListener != null) waypointListener.OnCloseWaypointWindow();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

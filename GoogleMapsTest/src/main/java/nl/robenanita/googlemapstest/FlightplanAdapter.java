package nl.robenanita.googlemapstest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.database.FlightPlanDataSource;
import nl.robenanita.googlemapstest.flightplan.FlightPlan;

/**
 * Created by Rob Verhoef on 23-3-14.
 */
public class FlightplanAdapter extends BaseAdapter {
    private static int selectedIndex = -1;
    private ArrayList<FlightPlan> flightPlans;
    private Context context;
    private String TAG = "GooglemapsTest";
    public FlightplanAdapter(ArrayList<FlightPlan> flightPlans, Context context)
    {
        this.flightPlans = flightPlans;
        selectedIndex = -1;
        this.context = context;
    }

    public void setSelectedIndex(int selectedIndex) {
        FlightplanAdapter.selectedIndex = selectedIndex;
    }

    @Override
    public int getCount() {
        return flightPlans.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public FlightPlan GetFlightplan(int i)
    {
        return flightPlans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.item_delete, viewGroup, false);

        Button textView = (Button) view.findViewById(R.id.text_item);
        final FlightPlan flightPlan = flightPlans.get(i);
        textView.setText(flightPlan.name);
        textView.setTag(i);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClick(view);
            }
        });

        ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.deleteBtn);
        deleteBtn.setTag(flightPlan);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FlightPlan f = (FlightPlan)view.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete the \r\n'" + f.name + "' flightplan")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            //NavigationActivity.super.onBackPressed();
                                FlightPlanDataSource flightPlanDataSource = new FlightPlanDataSource(context);
                                flightPlanDataSource.open();
                                flightPlanDataSource.DeleteFlightplan(f);
                                flightPlanDataSource.close();

                                flightPlans.remove(f);
                                notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        if (i == selectedIndex) {
            view.setBackgroundColor(Color.parseColor("#7ecce8"));
        }
        else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    private View.OnClickListener onClickListener = null;
    public void SetOnFlightplanClickListener(final View.OnClickListener c){ onClickListener = c; }

}

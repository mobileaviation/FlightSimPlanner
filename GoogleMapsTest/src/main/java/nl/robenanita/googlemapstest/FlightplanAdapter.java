package nl.robenanita.googlemapstest;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.flightplan.FlightPlan;

/**
 * Created by Rob Verhoef on 23-3-14.
 */
public class FlightplanAdapter extends BaseAdapter {
    private static int selectedIndex = -1;
    private ArrayList<FlightPlan> flightPlans;
    public FlightplanAdapter(ArrayList<FlightPlan> flightPlans)
    {
        this.flightPlans = flightPlans;
        selectedIndex = -1;
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
        view = inflater.inflate(R.layout.country_item, viewGroup, false);

        TextView textView = (TextView) view.findViewById(R.id.country_text_item);
        FlightPlan flightPlan = flightPlans.get(i);
        textView.setText(flightPlan.name);

        if (i == selectedIndex) {
            view.setBackgroundColor(Color.parseColor("#7ecce8"));
        }
        else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }
}

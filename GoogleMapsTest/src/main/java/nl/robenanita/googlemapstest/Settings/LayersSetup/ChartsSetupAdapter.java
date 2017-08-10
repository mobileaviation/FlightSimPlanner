package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Charts.AirportChart;
import nl.robenanita.googlemapstest.Charts.AirportCharts;
import nl.robenanita.googlemapstest.Continent;
import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 10-8-2017.
 */

public class ChartsSetupAdapter extends BaseAdapter {
    private AirportCharts airportCharts;

    public ChartsSetupAdapter(AirportCharts airportCharts) {
        this.airportCharts = airportCharts;
    }

    @Override
    public int getCount() {
        return airportCharts.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public AirportChart GetAirportChart(int i) {
        return this.airportCharts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.adapter_chartssetup, viewGroup, false);

        TextView textView = (TextView) view.findViewById(R.id.chartSetupTxt);
        AirportChart chart = GetAirportChart(i);
        textView.setText(chart.display_name);

        LinearLayout chartLayout = (LinearLayout) view.findViewById(R.id.chartSetupLayout);

        if ( (i & 1) == 0 ) {
            chartLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }
}

package nl.robenanita.googlemapstest.Weather;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 6-5-2014.
 */
public class TafAdapter extends BaseAdapter {
    public ArrayList<Taf> tafs;
    public TafAdapter(ArrayList<Taf> tafs) { this.tafs = tafs; }
    @Override
    public int getCount() {
        return tafs.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public Taf getTaf(int i)
    {
        return tafs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.metar_item, viewGroup, false);

        TextView tafStationNameTxt = (TextView) view.findViewById(R.id.metar_stationNameTxt);
        TextView tafStationIdTxt = (TextView) view.findViewById(R.id.metar_stationIdTxt);
        TextView tafDistanceTxt = (TextView) view.findViewById(R.id.metar_DistanceTxt);
        LinearLayout tafLayout = (LinearLayout) view.findViewById(R.id.metarAdapterLayout);


        Taf m = getTaf(i);

        tafStationNameTxt.setText((m.airport == null) ? m.station_id : m.airport.name);
        tafStationIdTxt.setText(m.station_id);
        tafDistanceTxt.setText(Integer.toString(Math.round(m.distance_to_org_m/1852f)) + " NM");

        if ( (i & 1) == 0 ) {
            tafLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }


        return view;
    }
}

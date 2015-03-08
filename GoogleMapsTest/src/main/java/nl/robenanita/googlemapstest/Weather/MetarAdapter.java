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
public class MetarAdapter extends BaseAdapter {
    public ArrayList<Metar> metars;
    public MetarAdapter(ArrayList<Metar> metars) { this.metars = metars; }
    @Override
    public int getCount() {
        return metars.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public Metar getMetar(int i)
    {
        return metars.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.metar_item, viewGroup, false);

        TextView metarStationNameTxt = (TextView) view.findViewById(R.id.metar_stationNameTxt);
        TextView metarStationIdTxt = (TextView) view.findViewById(R.id.metar_stationIdTxt);
        TextView metarDistanceTxt = (TextView) view.findViewById(R.id.metar_DistanceTxt);
        LinearLayout metarLayout = (LinearLayout) view.findViewById(R.id.metarAdapterLayout);


        Metar m = getMetar(i);

        metarStationNameTxt.setText((m.airport == null) ? m.station_id : m.airport.name);
        metarStationIdTxt.setText(m.station_id);
        metarDistanceTxt.setText(Integer.toString(Math.round(m.distance_to_org_m/1852f)) + " NM");

        if ( (i & 1) == 0 ) {
            metarLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }


        return view;
    }
}

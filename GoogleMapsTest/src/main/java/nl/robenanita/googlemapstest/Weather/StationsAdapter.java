package nl.robenanita.googlemapstest.Weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 11-3-2015.
 */
public class StationsAdapter extends BaseAdapter {
    private ArrayList<Station> stations;
    public StationsAdapter(ArrayList<Station> stations) { this.stations = stations; }

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public Object getItem(int i) {
        return stations.get(i);
    }


    public Station getStation(int i) { return  stations.get(i); }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.airport_info_stations_item, viewGroup, false);

        TextView stationText = (TextView) view.findViewById(R.id.airport_info_station_item_text);
        LinearLayout stationLayout = (LinearLayout) view.findViewById(R.id.airport_info_station_item_layout);

        Station m = stations.get(i);

        stationText.setText(m.station_id);

        if ( (i & 1) == 0 ) {
            //stationLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
            stationLayout.setBackgroundResource(R.drawable.border);

        }

        return view;
    }
}

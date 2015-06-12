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
 * Created by Rob Verhoef on 7-3-2015.
 */
public class MetarRawAdapter extends BaseAdapter {
    private ArrayList<Metar> metars;
    public MetarRawAdapter(ArrayList<Metar> metars) { this.metars = metars; }

    @Override
    public int getCount() {
        return metars.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.airport_info_item, viewGroup, false);

        TextView icaoText = (TextView) view.findViewById(R.id.airport_info_icaoText);
        TextView messageText = (TextView) view.findViewById(R.id.airport_info_messageText);
        LinearLayout notamLayout = (LinearLayout) view.findViewById(R.id.airport_info_item_layout);

        Metar m = metars.get(i);

        icaoText.setText(m.station_id);
        messageText.setText(m.raw_text);

        if ( (i & 1) == 0 ) {
            notamLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }

    public Metar getMetar(int i)
    {
        return metars.get(i);
    }
}

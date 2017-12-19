package nl.robenanita.googlemapstest.Airport;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 25-2-14.
 */
public class AirportAdapter extends BaseAdapter {
    private static int selectedIndex = -1;
    public Map<Integer, Airport> airports;

    public AirportAdapter(Map<Integer, Airport> airports)
    {
        this.airports = airports;
    }

    public static void setSelectedIndex(int selectedIndex) {
        AirportAdapter.selectedIndex = selectedIndex;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.country_item, viewGroup, false);

        TextView textView = (TextView) view.findViewById(R.id.country_text_item);
        Airport airport = (Airport)airports.values().toArray()[i];
        textView.setText("(" + airport.ident + ") " + airport.name);

        if (i == selectedIndex) {
            view.setBackgroundColor(Color.parseColor("#7ecce8"));
        }
        else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public Airport GetAirport(int i)
    {
        return (Airport)airports.values().toArray()[i];
    }

    @Override
    public int getCount() {
        return airports.size();
    }
}

package nl.robenanita.googlemapstest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 25-2-14.
 */
public class ContinentAdapter extends BaseAdapter {
    private ArrayList<Continent> continents;
    public ContinentAdapter(ArrayList<Continent> continents)
    {
        this.continents = continents;
    }

    @Override
    public int getCount() {
        return continents.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public Continent GetContinent(int i)
    {
        return this.continents.get(i);
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
        Continent continent = continents.get(i);
        textView.setText(continent.name);

        return view;
    }
}

package nl.robenanita.googlemapstest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 10-2-14.
 */
public class CountryAdapter extends BaseAdapter {
    private ArrayList<Country> countries;
    public CountryAdapter(ArrayList<Country> countries)
    {
        this.countries = countries;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public Country GetCountry(int i)
    {
        return this.countries.get(i);
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
        Country country = countries.get(i);
        textView.setText(country.name);

        return view;
    }
}


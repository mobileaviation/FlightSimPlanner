package nl.robenanita.googlemapstest.Weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 7-3-2015.
 */
public class TafRawAdapter extends BaseAdapter {
    private ArrayList<Taf> tafs;
    public TafRawAdapter(ArrayList<Taf> tafs) { this.tafs = tafs; }

    @Override
    public int getCount() {
        return tafs.size();
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
        //view = inflater.inflate(R.layout.metar_item, viewGroup, false);

        return view;
    }
}

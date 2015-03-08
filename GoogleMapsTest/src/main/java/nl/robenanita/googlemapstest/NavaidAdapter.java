package nl.robenanita.googlemapstest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by Rob Verhoef on 16-4-14.
 */
public class NavaidAdapter extends BaseAdapter {
    public Map<Integer, Navaid> navaids;

    public NavaidAdapter(Map<Integer, Navaid> navaids)
    {
        this.navaids = navaids;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.country_item, viewGroup, false);

        TextView textView = (TextView) view.findViewById(R.id.country_text_item);
        Navaid navaid = (Navaid)navaids.values().toArray()[i];
        textView.setText("(" + navaid.ident + ") " + navaid.name + " : " + navaid.type);

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

    public Navaid GetNavaid(int i)
    {
        return (Navaid)navaids.values().toArray()[i];
    }

    @Override
    public int getCount() {
        return navaids.size();
    }
}

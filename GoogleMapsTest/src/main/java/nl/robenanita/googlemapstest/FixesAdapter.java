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
public class FixesAdapter extends BaseAdapter {
    public Map<Integer, Fix> fixes;

    public FixesAdapter(Map<Integer, Fix> fixes)
    {
        this.fixes = fixes;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.country_item, viewGroup, false);

        TextView textView = (TextView) view.findViewById(R.id.country_text_item);
        Fix fix = (Fix)fixes.values().toArray()[i];
        textView.setText("(" + fix.ident + ") " + fix.name);

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

    public Fix GetFix(int i)
    {
        return (Fix)fixes.values().toArray()[i];
    }

    @Override
    public int getCount() {
        return fixes.size();
    }
}

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
public class NotamRawAdapter extends BaseAdapter {
    private ArrayList<Notam> notams;
    public NotamRawAdapter(ArrayList<Notam> notams) { this.notams = notams; }

    @Override
    public int getCount() {
        return notams.size();
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

        Notam m = notams.get(i);

        icaoText.setText(m.getStation_id());
        messageText.setText(m.GetMessage());

        if ( (i & 1) == 0 ) {
            notamLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }

    public Notam getNotam(int i)
    {
        return notams.get(i);
    }
}

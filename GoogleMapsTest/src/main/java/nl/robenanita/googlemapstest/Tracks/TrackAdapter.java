package nl.robenanita.googlemapstest.Tracks;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;

/**
 * Created by Rob Verhoef on 23-3-14.
 */
public class TrackAdapter extends BaseAdapter {
    private static int selectedIndex = -1;
    private ArrayList<LocationTrackingDataSource.Track> tracks;
    public TrackAdapter(ArrayList<LocationTrackingDataSource.Track> tracks)
    {
        this.tracks = tracks;
        selectedIndex = -1;
    }

    public void setSelectedIndex(Integer ind)
    {
        selectedIndex = ind;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public LocationTrackingDataSource.Track GetTracks(int i)
    {
        return tracks.get(i);
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
        LocationTrackingDataSource.Track track = tracks.get(i);
        textView.setText(track.name);

        if (i == selectedIndex) {
            view.setBackgroundColor(Color.parseColor("#7ecce8"));
        }
        else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }
}
